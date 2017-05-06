package i3k.gtool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import i3k.gtool.KeyGen.EncryptKeyCode;
import i3k.gtool.KeyGen.RandomCodeGen;


public class KeyGen {
	
	private static final int CODE_SEGMENT_COUNT = 4;
	private static final int CODE_SEGMENT_LENGTH = 4;
	private static final String PROPERTIES_FILE_SEPARATOR_CHAR = "_";
    private static final String PROPERTIES_FILE_EXTENSION_NAME = ".properties";
    private static final String CDKEY_FILE_EXTENSION_NAME = ".txt";
    private static final String KEY_SQL_FILE_EXTENSION_NAME = ".sql";
    private static final String BATCH_ID_PROPERTY_NAME = "batchId";
    private static final String KEY_NAME_PREFIX_IN_PROPERTIES = "key_";
	
	public static final int[][] SEQ_TBL = 
	{
		{0, 1, 2, 3}, {0, 1, 3, 2}, {0, 2, 1, 3}, {0, 2, 3, 1}, {0, 3, 1, 2}, {0, 3, 2, 1},
		{1, 0, 2, 3}, {1, 0, 3, 2}, {1, 2, 0, 3}, {1, 2, 3, 0}, {1, 3, 0, 2}, {1, 3, 2, 0},
		{2, 0, 1, 3}, {2, 0, 3, 1}, {2, 1, 0, 3}, {2, 1, 3, 0}, {2, 3, 0, 1}, {2, 3, 1, 0},
		{3, 0, 1, 2}, {3, 0, 2, 1}, {3, 1, 0, 2}, {3, 1, 2, 0}, {3, 2, 0, 1}, {3, 2, 1, 0},
	};
	
	// define static classes
	static class CodeChars
	{
		private static Random random = new Random();
		private static final char[] VALID_CHARS = 
		{
		    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
		};
		private static final Set<Character> VALID_CHAR_SET = new HashSet<Character>();
		static 
		{
			for (int i = 0; i < VALID_CHARS.length; ++i)
			{
				VALID_CHAR_SET.add(VALID_CHARS[i]);
			}
		}
		
		public static boolean isCharValid(char c)
		{
			return VALID_CHAR_SET.contains(c);
		}
		
		public static int getCharsCount()
		{
			return VALID_CHARS.length;
		}
		
		public static char getChar(int index)
		{
			return VALID_CHARS[index];
		}
		
		public static char genRandomChar()
		{
			return VALID_CHARS[random.nextInt(VALID_CHARS.length)];
		}
		public static char getByteChar(byte b)
		{
			int val = 0xff & b;
			return VALID_CHARS[val%VALID_CHARS.length];
		}
	}
	static class RandomCodeGen
	{
		public RandomCodeGen()
		{
			
		}
		
		public static Collection<String> genBatchCodes(int codeLength, int count)
		{
			Set<String> codes = new HashSet<String>(count);
			while (codes.size() < count)
			{
				String code = genRandomCode(codeLength);
				codes.add(code);
			}
			return codes;
		}
		
		private static String genRandomCode(int codeLength)
		{
			StringBuilder code = new StringBuilder();
	        for (int i = 0; i < codeLength; ++i) 
	        {
	        	code.append(CodeChars.genRandomChar());
	        }
	        return code.toString();
		}
	}
	
	static class EncryptKeyCode
	{
		private String key = "ksladslfghfkjhk";
		private MessageDigest md;
		public EncryptKeyCode()
		{
			try
			{
				md = java.security.MessageDigest.getInstance("MD5");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
		}
		
		private byte[] genEncryptCode(String rawCode)
		{
			StringBuilder keyCode = new StringBuilder(key);
			keyCode.append(rawCode);
			return md.digest(keyCode.toString().getBytes());
		}
		
		public String genEncryptCodeDigest(String rawCode, int digestLength)
		{
			byte[] encryptCode = genEncryptCode(rawCode);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < digestLength; ++i)
			{
				sb.append(CodeChars.getByteChar(encryptCode[i]));
			}
			return sb.toString();
		}
	}
	
	public static class CDKeyIDInfo
	{
		public int batchID = -1;
		public int genID = -1;
	}
	

	
	
	// functions
	public static int getInitCodeOffset(String code)
	{
    	int offset = 0;
    	for (int i = 0; i < code.length(); ++i) 
    	{
    		offset = Character.getNumericValue(code.charAt(i)) + offset*CodeChars.getCharsCount();
    	}
    	return offset;
    }
	public static String genIDCode(int Id, String initCode) 
	{
		int initCodeIdOffset = getInitCodeOffset(initCode);
    	int offsetBatchId = initCodeIdOffset + Id;
    	int codeCount = 1;
    	for (int i = 0; i < initCode.length(); ++i)
    		codeCount *= CodeChars.getCharsCount();
    	offsetBatchId %= codeCount;
    	char[] code = new char[initCode.length()];
    	for (int i = 0; i < initCode.length(); ++i) 
    	{
    		int radix = 1;
    		for (int j = 0; j < initCode.length()-1-i; ++j)
    			radix *= CodeChars.getCharsCount();
    		int index = offsetBatchId/radix%CodeChars.getCharsCount();
    		code[i] = CodeChars.getChar(index);
    	}
    	return new String(code);
    }
	
	
	public static int calcCodeID(String code, String initCode) 
	{
		int initCodeIdOffset = getInitCodeOffset(initCode);
    	int id = -1;
    	int idOffset = 0;
		for (int i = 0; i < code.length(); ++i) 
		{
			idOffset = Character.getNumericValue(code.charAt(i)) + idOffset*CodeChars.getCharsCount();
    	}
		int batchCodeMax = 1;
    	for (int i = 0; i < code.length(); ++i)
    		batchCodeMax *= CodeChars.getCharsCount();
    	id = (batchCodeMax + idOffset - initCodeIdOffset)%batchCodeMax;
    	return id;
    }
	
	static int getSumMod(char[] code, int m)
	{
		int sum = 0;
		for (int i = 0; i < code.length; ++i)
		{
			sum += Character.getNumericValue(code[i]);
		}
		return sum%m;
	}
	static int getSum(String code)
	{
		int sum = 0;
		for (int i = 0; i < code.length(); ++i)
		{
			sum += Character.getNumericValue(code.charAt(i));
		}
		return sum;
	}
	static int getSumMod(String code, int m)
	{
		int sum = getSum(code);
		return sum%m;
	}
	static int getSumMod(String[] code, int m)
	{
		int sum = 0;
		for (int i = 0; i < code.length; ++i)
		{
			sum += getSum(code[i]);
		}
		return sum%m;
	}
	static String[] shuffleCodeRevert(String[] shuffleCodes)
	{
		int seqIndex = getSumMod(shuffleCodes, SEQ_TBL.length);
		int[] seqtbl = SEQ_TBL[seqIndex];
		String[] rawCodes = new String[shuffleCodes.length];
		for (int i = 0; i < rawCodes.length; ++i)
		{
			StringBuilder sb = new StringBuilder();
			int index = seqtbl[i];
			for (int j = 0; j < shuffleCodes.length; ++j)
			{
				sb.append(shuffleCodes[j].charAt(index));
			}
			rawCodes[i] = sb.toString(); 
		}
		return rawCodes;
	}
	static String[] revertCode(String codes)
	{
		String[] shuffleCodes = new String[CODE_SEGMENT_COUNT];
		for (int i = 0; i < shuffleCodes.length; ++i)
		{
			shuffleCodes[i] = codes.substring(i*CODE_SEGMENT_LENGTH, (i+1)*CODE_SEGMENT_LENGTH);
		}
		String[] rawCodes = shuffleCodeRevert(shuffleCodes);
		for (int i = 0; i < rawCodes.length; ++i)
		{
			rawCodes[i] = shuffleCodeRevert(rawCodes[i]);
		}
		return rawCodes;
	}
	static String getCodeBatch(EncryptKeyCode ekc, String code)
	{
		String batchCode = null;
		String[] rawCodes = revertCode(code);
		StringBuilder rawCode = new StringBuilder();
		for (int i = 0; i < rawCodes.length-1; ++i)
		{
			rawCode.append(rawCodes[i]);
		}
		String checkCode = ekc.genEncryptCodeDigest(rawCode.toString(), CODE_SEGMENT_LENGTH);
		if (checkCode.equals(rawCodes[rawCodes.length-1]))
		{
			batchCode = rawCodes[0].toString();
		}
		return batchCode;
	}
	static String shuffleCodeRevert(String shuffleCode)
	{
		int seqIndex = getSumMod(shuffleCode, SEQ_TBL.length);
		int[] seqtbl = SEQ_TBL[seqIndex];
		StringBuilder rawCode = new StringBuilder();
		for (int i = 0; i < shuffleCode.length(); ++i)
		{
			int index = seqtbl[i];
			rawCode.append(shuffleCode.charAt(index));
		}
		return rawCode.toString();
	}
	
	static boolean checkCodeFormatValid(String code)
	{
		if (code.length() != CODE_SEGMENT_COUNT*CODE_SEGMENT_LENGTH)
			return false;
		for (int i = 0; i < code.length(); ++i)
		{
			char c = code.charAt(i);
			if (!CodeChars.isCharValid(c))
				return false;
		}
		return true;
	}
	
	public static int getCodeLength()
	{
		return  CODE_SEGMENT_COUNT*CODE_SEGMENT_LENGTH;
	}
	
	static String shuffleCodeGen(String rawCode)
	{
		int seqIndex = KeyGen.getSumMod(rawCode, KeyGen.SEQ_TBL.length);
		int[] seqtbl = KeyGen.SEQ_TBL[seqIndex];
		StringBuilder shuffleCode = new StringBuilder();
		shuffleCode.append(rawCode);
		for (int i = 0; i < shuffleCode.length(); ++i)
		{
			int index = seqtbl[i];
			shuffleCode.setCharAt(index, rawCode.charAt(i));
		}
		return shuffleCode.toString();
	}
	static StringBuilder[] shuffleCodeGen(String[] rawCodes)
	{
		int seqIndex = KeyGen.getSumMod(rawCodes, KeyGen.SEQ_TBL.length);
		int[] seqtbl = KeyGen.SEQ_TBL[seqIndex];
		StringBuilder[] shuffleCodes = new StringBuilder[rawCodes.length];
		for (int i = 0; i < shuffleCodes.length; ++i)
		{
			shuffleCodes[i] = new StringBuilder(rawCodes[i]);
		}
		for (int i = 0; i < shuffleCodes.length; ++i)
		{
			int index = seqtbl[i];
			String code = rawCodes[i];
			for (int j = 0; j < code.length(); ++j)
			{
				shuffleCodes[j].setCharAt(index, code.charAt(j));
			}
		}
		return shuffleCodes;
	}
	static String shuffleCode(String[] rawCodes)
	{
		String[] shuffleCodes = new String[rawCodes.length];
		for (int i = 0; i < shuffleCodes.length; ++i)
		{
			shuffleCodes[i] = shuffleCodeGen(rawCodes[i]);
		}
		StringBuilder[] shuffleCodesSeg = shuffleCodeGen(shuffleCodes);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < shuffleCodesSeg.length; ++i)
		{
			sb.append(shuffleCodesSeg[i]);
		}
		return sb.toString();
	}
//	static String genCode(String batchCode, RandomCodeGen rcg, EncryptKeyCode ekc)
//	{
//		String[] rawCodes = new String[CODE_SEGMENT_COUNT];
//		int index = 0;
//		rawCodes[index++] = batchCode;
//		rawCodes[index++] = rcg.genNext(CODE_SEGMENT_LENGTH);
//		rawCodes[index++] = rcg.genNext(CODE_SEGMENT_LENGTH);
//		StringBuilder codes = new StringBuilder();
//		for (int i = 0; i < rawCodes.length-1; ++i)
//		{
//			codes.append(rawCodes[i]);
//		}
//		rawCodes[index++] = ekc.genEncryptCodeDigest(codes.toString(), CODE_SEGMENT_LENGTH);
//		return shuffleCode(rawCodes);
//	}
//	
//	static public List<String> genCodes(String batchCode, int count)
//	{
//		List<String> codes = new ArrayList<String>();
//		RandomCodeGen rcg = new RandomCodeGen(count);
//		EncryptKeyCode ekc = new EncryptKeyCode();
//		for (int i = 0; i < count; ++i)
//		{
//			codes.add(genCode(batchCode, rcg, ekc));
//		}
//		return codes;
//	}
	
	static public List<String> genCodes(String batchCode, int count)
	{
		List<String> codes = new ArrayList<>();
		{
			Collection<String> rawRandomCodes =  RandomCodeGen.genBatchCodes(CODE_SEGMENT_LENGTH * (CODE_SEGMENT_COUNT - 2), count);
			String[] rawCodes = new String[CODE_SEGMENT_COUNT];
			{
				EncryptKeyCode ekc = new EncryptKeyCode();
				for (String rawRandomCode : rawRandomCodes)
				{
					rawCodes[0] = batchCode;
					for (int index = 1; index < CODE_SEGMENT_COUNT - 1; ++index)
					{
						rawCodes[index] = rawRandomCode.substring(CODE_SEGMENT_LENGTH * (index-1), CODE_SEGMENT_LENGTH * index);
					}
					rawCodes[CODE_SEGMENT_COUNT - 1] = ekc.genEncryptCodeDigest(batchCode + rawRandomCode, CODE_SEGMENT_LENGTH);
					String code = shuffleCode(rawCodes);
					codes.add(code);
				}
			}
		}
		return codes;
	}
	
	
	/**
	 * 根据当前路径下的文件，获取下一个生成的批次号
	 */
	public static class BatchGenCDKeyFileFilter implements FilenameFilter
    {
    	int batchID = -1;
    	String filePrefixName = "";
    	String fileExtensionName = "";
    	public BatchGenCDKeyFileFilter() 
    	{
    		
    	}
    	public BatchGenCDKeyFileFilter(int batchID) 
    	{
    		this.batchID = batchID;
    	}
    	
    	public BatchGenCDKeyFileFilter(String filePrefixName, String fileExtensionName)
    	{
    		this.filePrefixName = filePrefixName;
    		this.fileExtensionName = fileExtensionName;
    	}
		@Override
		public boolean accept(File dir, String name) {
			return false;
		}
    }
	private static String[] getAllBatchCDKeyFileNames(String dir, BatchGenCDKeyFileFilter fileFilter) 
    {
    	File curfile = new File(dir);
    	String[] files = curfile.list(fileFilter);
    	return files;
    }
	
	public static int getNextBatchId(String dir, BatchGenCDKeyFileFilter fileFilter) 
    {
    	int maxBatchId = 0;
    	String[] allBatchCDKeyFileNames = getAllBatchCDKeyFileNames(dir, fileFilter);
    	for (String name : allBatchCDKeyFileNames) 
    	{
    		String batchGenIdStr = name.substring(fileFilter.filePrefixName.length(), name.length() - fileFilter.fileExtensionName.length());
    		String[] childStrs = batchGenIdStr.split(PROPERTIES_FILE_SEPARATOR_CHAR);
			int batchid = Integer.parseInt(childStrs[0]);
			if (batchid > maxBatchId)
				maxBatchId = batchid;
    	}
    	return maxBatchId + 1;
    }
	
	/**
	 * 保存到文件相关
	 */
	private static String getPropertiesFileName(int batchId, String name)
    {
    	return name + "_" + batchId + PROPERTIES_FILE_EXTENSION_NAME;
    }
    
    private static String getResultFileName(int batchId, String name) 
    {
    	return name + "-"  + batchId + CDKEY_FILE_EXTENSION_NAME;
    }
    
    private static String getSqlFileName(int batchId, String name) 
    {
    	return name + batchId  + KEY_SQL_FILE_EXTENSION_NAME;
    }
    
	public static void saveGenCDKeyProperties(int batchID, List<String> cdkeys, String dir, String name) 
	{
    	try 
    	{
        	Properties properties = new Properties();
        	properties.setProperty(BATCH_ID_PROPERTY_NAME, String.valueOf(batchID));
        	System.out.println("......save batch gen info properties: batchId=" + batchID);
        	int seqNo = 1;
        	for (String cdkey : cdkeys)
        	{
        		String propertyName = KEY_NAME_PREFIX_IN_PROPERTIES + seqNo++;
        		properties.setProperty(propertyName, cdkey);
        	}
        	System.out.println("......save batch gen cdkey properties: batchId=" + batchID + ", keyCount=" + cdkeys.size());
        	String propertiesFileName = dir + File.separator + getPropertiesFileName(batchID, name);
            FileOutputStream out = new FileOutputStream(propertiesFileName);
            properties.store(out, "gen cdkeys batchID="+batchID+", at " + new Date());
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
	
	public static void saveGenCDkeyResult(int batchID, List<String> cdkeys, String dir, String name) 
	{
    	try 
    	{
    		String cdKeyResultFileName = dir + File.separator +  getResultFileName(batchID, name);
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
	
	private static String createCDKeyInsertSql(int batchid, String cdkey) 
    {
		return String.format("INSERT INTO keyinfo(kvalue) VALUES('%s');\n",  cdkey);
    }
	
	public static void saveGenCDkeySqlResult(int batchId, List<String> cdkeys, String dir, String name) 
	{
		try 
		{
			String genCDKeySqlFileName = dir + File.separator +  getSqlFileName(batchId, name);
			
	    	Writer sqlwriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(genCDKeySqlFileName), "UTF-8"));
			sqlwriter.write("use activekeys;\n");
			sqlwriter.write("SET collation_connection = utf8_bin;\n");
			sqlwriter.write("SET character_set_client = utf8;\n");
			sqlwriter.write("SET character_set_connection = utf8;\n");
			sqlwriter.write("SET autocommit=0;\n");
			
			for (String cdkey : cdkeys) 
			{
				String cdkeySql = createCDKeyInsertSql(batchId, cdkey);
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
}
