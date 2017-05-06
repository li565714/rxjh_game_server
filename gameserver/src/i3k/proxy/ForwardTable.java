package i3k.proxy;


import i3k.SBean;
import i3k.gs.GameData;
import i3k.gs.GameConf.GameConfig;
import i3k.util.GameTime;
import i3k.util.XmlElement;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;
import java.io.BufferedInputStream;
import java.io.FileInputStream;






import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import ket.kio.NetAddress;


public class ForwardTable 
{
	IDIPProxyServer ps;
	Map<Integer, NetAddress> gsId2NetAddrs = new HashMap<>();
	public ForwardTable(IDIPProxyServer ps)
	{
		this.ps = ps;
	}
	
	public NetAddress getFowardAddress(int id)
	{
		return gsId2NetAddrs.get(id);
	}
	
	public void start()
	{
		ps.getResourceManager().addWatch(ps.getConfig().forwardTableFileName, this::loadConfigureFile);
	}
	
	public void destroy()
	{
		
	}
	
	void onTimer(int timeTick)
	{
		
	}
	
	public void loadConfigureFile(String filepath)
	{
		ps.getLogger().info("forward table config file ("+filepath+") changed, try reload and parse config file...");
		try
		{
			setConfigs(parseConfigs(XmlElement.parseXml(filepath)));
			ps.getLogger().info("forward table config file ("+filepath+") reload and parse success.");
		}
		catch (Throwable t)
		{
			ps.getLogger().warn("load forward table config file ("+filepath+")  failed !!!", t);
		}
	}
	
	Map<Integer, NetAddress> parseConfigs(XmlElement root) throws Exception
	{
		Map<Integer, NetAddress> tbl = new HashMap<>();
		if (root.getName().equals("forwardtable"))
		{
			for (XmlElement e : root.getChildrenByName("server"))
			{
				int id = e.getIntegerAttribute("id");
				String ip = e.getStringAttribute("ip");
				int port = e.getIntegerAttribute("port");
				tbl.put(id, new NetAddress(ip, port));
			}
		}
		return tbl;
	}
	
	void setConfigs(Map<Integer, NetAddress> gsId2NetAddrs)
	{
		this.gsId2NetAddrs = gsId2NetAddrs;
	}
	

}





