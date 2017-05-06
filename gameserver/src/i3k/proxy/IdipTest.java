package i3k.proxy;

import i3k.IDIP;
import i3k.util.XmlElement;
import i3k.util.XmlElement.XmlNodeNotFoundException;
import i3k.util.XmlElement.XmlReadException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ket.util.ArgsMap;
import ket.util.Stream;
import ket.util.Stream.AIStream;
import ket.util.Stream.BytesOutputStream;
import ket.util.Stream.DecodeException;
import ket.util.Stream.EOFException;
import i3k.util.GameTime;

public class IdipTest
{
	public static final int MSGWRITER = 0;
	public static final int ERRWRITER = 1;
	private static String type;
	private static String ip;
	private static int port;
	private static String configFile;
	private static int sendTimes;
	private static int threadcount;
	private static long sleepTime;
	private static String requestPath;
	private static String reportPath;
	private static String help;
	private static Map<String, List<String>> cmdIdPara = new HashMap<String, List<String>>();
	private static Map<String, List<String>> diyStruct = new HashMap<String, List<String>>();
	private static Map<String, Integer> staticNum = new HashMap<String, Integer>();
	private static int seq = 0;
	private static int headlength = 0;
	private static int bodylength = 0;
	private static int lastint = 0;
	private static BufferedWriter reportWrite = null;
	private static volatile int sendNum = 0;
	private static volatile int currentThreadNum = 0;
	private static volatile int successNum = 0;

	public static void main(String[] args)
	{
		ArgsMap am = new ArgsMap(args);
		type = am.get("-type", "func");
		ip = am.get("-ip", "127.0.0.1");
		port = Integer.parseInt(am.get("-port", "9100"));
		configFile = am.get("-config_file", "./conf.xml");
		sendTimes = Integer.parseInt(am.get("-send_times", "1"));
		threadcount = Integer.parseInt(am.get("-thread_count", "1"));
		sleepTime = Long.parseLong(am.get("-sleep_time", "0"));
		requestPath = am.get("-request_file", "request.txt");
		reportPath = am.get("-report_file", null);
		help = am.get("-help", "true");
		if (type.equals("func"))
		{
			sendTimes = 1;
			threadcount = 1;
			sleepTime = 0;
		}
		BufferedReader reader = null;
		if (reportPath != null && !reportPath.isEmpty())
			initReportFile();
		try
		{
			XmlElement root = XmlElement.parseXml("./qsg_idip.xml");
			initMap(root);
			for (int i = 0; i < threadcount; i++)
			{
				new IdipTest().new IdipTestRun("thread " + i).start();
			}
			while (currentThreadNum != 0)
			{
			}
			writeInfo("MainResult=====send " + sendNum + " packet, success " + successNum + " packet, success rate = " + successNum * 100 / sendNum + "%", MSGWRITER);
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
		finally
		{
			if (reader != null)
				try
				{
					reader.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			if (reportWrite != null)
				try
				{
					reportWrite.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
		}
	}

	private static void initReportFile()
	{
		File reportfile = new File(reportPath);
		if (!reportfile.exists())
		{
			try
			{
				reportfile.createNewFile();
			}
			catch (IOException e)
			{
				writeInfo("report file create fail, please check the filepath!", ERRWRITER);
				e.printStackTrace();
				return;
			}
		}
		if (!reportfile.isFile())
		{
			writeInfo("reportPath is not a file!", ERRWRITER);
			return;
		}
		try
		{
			reportWrite = new BufferedWriter(new FileWriter(reportfile));
		}
		catch (IOException e)
		{
			writeInfo("create writer fail!", ERRWRITER);
			e.printStackTrace();
			return;
		}
	}

	private static void initMap(XmlElement root) throws XmlNodeNotFoundException, XmlReadException
	{
		XmlElement cmdid = null;
		XmlElement staticnum = null;
		for (XmlElement macros : root.getChildrenByName("macrosgroup"))
		{
			if (macros.getAttribute("name").equals("NET_CMD_ID"))
				cmdid = macros;
			if (macros.getAttribute("name").equals("NET_MACRO"))
				staticnum = macros;
		}
		Map<String, String> cmdNameId = new HashMap<String, String>();
		for (XmlElement staticnumitem : staticnum.getChildrenByName("macro"))
		{
			staticNum.put(staticnumitem.getAttribute("name"), staticnumitem.getIntegerAttribute("value"));
		}
		for (XmlElement cmdiditem : cmdid.getChildrenByName("macro"))
		{
			cmdNameId.put(cmdiditem.getAttribute("name"), cmdiditem.getAttribute("value"));
		}
		for (XmlElement structitem : root.getChildrenByName("struct"))
		{
			if (!structitem.hasAttribute("id"))
			{
				String structname = structitem.getAttribute("name");
				diyStruct.put(structname, new ArrayList<String>());
				for (XmlElement paraitem : structitem.getChildrenByName("entry"))
				{
					String parastring = paraitem.getAttribute("name") + "," + paraitem.getAttribute("type");
					if (paraitem.hasAttribute("size"))
						parastring += "," + staticNum.get(paraitem.getAttribute("size"));
					diyStruct.get(structname).add(parastring);
				}
			}
			else
			{
				String structid = cmdNameId.get(structitem.getAttribute("id"));
				cmdIdPara.put(structid, new ArrayList<String>());
				for (XmlElement paraitem : structitem.getChildrenByName("entry"))
				{
					String parastring = paraitem.getAttribute("name") + "," + paraitem.getAttribute("type");
					if (paraitem.hasAttribute("size"))
						parastring += "," + staticNum.get(paraitem.getAttribute("size"));
					cmdIdPara.get(structid).add(parastring);
				}
			}
		}
	}

	private static int getXInteger(String cmd)
	{
		int num = 0;
		cmd = cmd.substring(2);
		for (int i = 0; i < cmd.length(); i++)
		{
			num *= 16;
			char curchar = cmd.charAt(i);
			if (curchar >= '0' && curchar <= '9')
				num += curchar - '0';
			else if (curchar >= 'a' && curchar <= 'f')
				num += curchar - 'a' + 10;
			else if (curchar >= 'A' && curchar <= 'F')
				num += curchar - 'A' + 10;
			else
				writeInfo("error cmd: " + cmd + "!", ERRWRITER);
		}
		return num;
	}

	private static void writeInfo(String info, int type)
	{
		if (reportWrite != null)
			try
			{
				reportWrite.append(info + "\r\n");
			}
			catch (IOException e)
			{
				System.err.print("write report file error!");
				e.printStackTrace();
			}
		if (type == MSGWRITER)
			System.out.println(info);
		else if (type == ERRWRITER)
			System.err.println(info);
	}

	public class IdipTestRun extends Thread
	{

		public IdipTestRun()
		{
			currentThreadNum++;
		}

		public IdipTestRun(String name)
		{
			super(name);
			currentThreadNum++;
		}

		@Override
		public void run()
		{
			BufferedReader reader;
			try
			{
				for (int i = 0; i < sendTimes; i++)
				{
					reader = new BufferedReader(new FileReader(requestPath));
					reader.lines().forEach(lineString ->
					{
						writeInfo("thread " + this.getId() + "=====" + doForEveryCmd(lineString), MSGWRITER);
					});
				}
			}
			catch (FileNotFoundException e)
			{
				writeInfo("thread " + this.getId() + "=====request file can not find!", ERRWRITER);
				e.printStackTrace();
			}
			finally
			{
				currentThreadNum--;
			}
		}

		private String doForEveryCmd(String lineString)
		{
			if (sleepTime > 0)
				try
				{
					Thread.sleep(sleepTime);
				}
				catch (InterruptedException e)
				{
					writeInfo("thread " + this.getName() + " sleep catch a exception", ERRWRITER);
					e.printStackTrace();
				}
			sendNum++;
			writeInfo("thread " + this.getId() + "=====this cmd is : " + lineString, MSGWRITER);
			Map<String, String> valueMap = new HashMap<String, String>();
			for (String lineentry : lineString.split("&"))
			{
				if (lineentry.equals(""))
					continue;
				String[] keyvalue = lineentry.split("=");
				if (keyvalue.length < 2)
					continue;
				valueMap.put(keyvalue[0].trim(), keyvalue[1].trim());
			}
			String cmd = valueMap.get("cmd");
			if (!cmdIdPara.containsKey(cmd))
			{
				writeInfo("thread " + this.getId() + "=====please check: (" + lineString + ") unvalid cmd!", ERRWRITER);
				return null;
			};

			BytesOutputStream bsos = new BytesOutputStream();
			Stream.AOStream os = new Stream.OStreamBE(bsos);
			for (String para : cmdIdPara.get(cmd))
			{
				if (para.equals(""))
					continue;
				String[] paras = para.split(",");
				if (!valueMap.containsKey(paras[0]))
				{
					writeInfo("thread " + this.getId() + "=====please check:(" + lineString + ") need (" + paras[0] + ") param£¡", ERRWRITER);
					continue;
				}
				String value = valueMap.get(paras[0]);
				if (paras[1].equals("uint32"))
					os.pushInteger(Integer.parseInt(value));
				if (paras[1].equals("int32"))
					os.pushInteger(Integer.parseInt(value));
				if (paras[1].equals("string"))
					os.pushString(value, Integer.parseInt(paras[2]));
				if (paras[1].equals("CommonItems"))
				{
					int size = Integer.parseInt(paras[2]);
					int index = 0;
					for (String item : value.split(";"))
					{
						if (index == size)
							break;
						if (item.equals(""))
							continue;
						String[] itemIdNum = para.split("_");
						if (itemIdNum.length < 2)
							continue;
						os.pushInteger(Integer.parseInt(itemIdNum[0]));
						os.pushInteger(Integer.parseInt(itemIdNum[1]));
						index++;
					}
				}
			}
			if (cmdIdPara.get(cmd).size() == 0)
				os.pushInteger(0);
			return sendPacket(Integer.parseInt(valueMap.get("Partition")), getHead(getXInteger(cmd), bsos), getBody(bsos));
		}

		private String sendPacket(int partition, byte[] head, byte[] body)
		{
			Socket client = null;
			String result = new String();
			try
			{
				client = new Socket(ip, port);
				client.setSoTimeout(10000);
				client.getOutputStream().write(head, 0, headlength);
				client.getOutputStream().write(body, 0, bodylength);
				int resultsize = client.getReceiveBufferSize();
				byte[] resultbytes = new byte[resultsize];
				client.getInputStream().read(resultbytes, 0, resultsize);
				Stream.BytesInputStream bais = new Stream.BytesInputStream(resultbytes, 0, resultsize);
				Stream.AIStream is = new Stream.IStreamBE(bais);
				result = getRspInfo(is);
			}
			catch (NumberFormatException e)
			{
				writeInfo("thread " + this.getId() + "=====port is not a number!", ERRWRITER);
				e.printStackTrace();
			}
			catch (UnknownHostException e)
			{
				writeInfo("thread " + this.getId() + "=====the host is can not connect!", ERRWRITER);
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					if (client != null)
						client.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			return result;
		}

		private String getRspInfo(AIStream is)
		{
			StringBuffer result = new StringBuffer();
			try
			{
				int cmdid = getRequestHead(result, is);
				getRequestBody(result, cmdid, is);
			}
			catch (EOFException e)
			{
				e.printStackTrace();
			}
			catch (DecodeException e)
			{
				e.printStackTrace();
			}
			return result.toString();
		}

		private void getRequestBody(StringBuffer result, int cmdid, AIStream is) throws EOFException, DecodeException
		{
			for (String para : cmdIdPara.get("0x" + Integer.toHexString(cmdid)))
			{
				if (para.equals(""))
					continue;
				String[] paras = para.split(",");
				appendResultPara(result, paras, is, false);
			}
		}

		private void appendResultPara(StringBuffer result, String[] paras, AIStream is, boolean isdiy) throws EOFException, DecodeException
		{
			int intval = 0;
			if (paras[1].equals("uint32"))
			{
				intval = is.popInteger();
				if (!isdiy)
					lastint = intval;
				result.append("\n" + paras[0] + " value is : " + intval);
			}
			else if (paras[1].equals("int32"))
			{
				intval = is.popInteger();
				if (!isdiy)
					lastint = intval;
				result.append("\n" + paras[0] + " value is : " + intval);
			}
			else if (paras[1].equals("string"))
			{
				result.append("\n" + paras[0] + " value is : " + is.popString(Integer.parseInt(paras[2])));
			}
			else
			{
				result.append("\n" + paras[0] + " {");
				for (int i = 0; i < lastint; i++)
				{
					result.append("\n(");
					for (String diypara : diyStruct.get(paras[1]))
					{
						if (diypara.equals(""))
							continue;
						String[] diyparas = diypara.split(",");
						appendResultPara(result, diyparas, is, true);
					}
					result.append("\n)");
				}
				result.append("\n}");
			}
		}

		public byte[] getBody(BytesOutputStream bsos)
		{
			bodylength = bsos.size();
			return bsos.array();
		}

		private int getRequestHead(StringBuffer result, AIStream is) throws EOFException, DecodeException
		{
			result.append("packet size is : " + is.popInteger());
			int cmdid = is.popInteger();
			result.append(",cmdid is : 0x" + Integer.toHexString(cmdid));
			result.append(",seq is : " + is.popInteger());
			result.append(",service name is : " + is.popString(staticNum.get("SERVICE_NAME_LENGTH")));
			result.append(",sendtime is : " + is.popInteger());
			result.append(",version is : " + is.popInteger());
			result.append(",authenticate is : " + is.popString(staticNum.get("AUTHENTICATE_LENGTH")));
			int resultnum = is.popInteger();
			if (resultnum == IDIP.IDIP_HEADER_RESULT_SUCCESS)
				successNum++;
			result.append(",result is : " + resultnum);
			result.append(",errormsg is : " + is.popString(staticNum.get("ERROR_MSG_LENGTH")));
			result.append("\r\nbody:");
			return cmdid;
		}

		public byte[] getHead(int cmdid, BytesOutputStream bsos)
		{
			BytesOutputStream heados = new BytesOutputStream();
			Stream.AOStream hos = new Stream.OStreamBE(heados);
			hos.pushInteger(IDIP.PACKET_HEADER_SIZE + bsos.size());
			hos.pushInteger(cmdid);
			hos.pushInteger(seq++);
			hos.pushString("IDIP", staticNum.get("SERVICE_NAME_LENGTH"));
			hos.pushInteger(GameTime.getGMTTime());
			hos.pushInteger(0);
			hos.pushString("ctx", staticNum.get("AUTHENTICATE_LENGTH"));
			hos.pushInteger(0);
			hos.pushString("", staticNum.get("ERROR_MSG_LENGTH"));
			headlength = heados.size();
			return heados.array();
		}

	}

}
