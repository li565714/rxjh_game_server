
package i3k.util;


import java.util.List;
import java.util.ArrayList;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.CDATASection;

import java.io.File;

import org.apache.log4j.Logger;



public class XmlElement
{
	public static class XmlReadException extends Exception
	{
		public XmlReadException(String message)
		{
			super(message);
		}
		
		public XmlReadException(String message, Throwable t)
		{
			super(message, t);
		}
	}
	
	public static class XmlNodeNotFoundException extends Exception
	{
		public XmlNodeNotFoundException(String message)
		{
			super(message);
		}
		
		public XmlNodeNotFoundException(String message, Throwable t)
		{
			super(message, t);
		}
	}
	
	Element element;
	public XmlElement(Element element)
	{
		this.element = element;
	}
	
	
	public static XmlElement parseXml(String filePath) throws Exception
	{
//		try
//		{
//			
//		}
//		catch(ParserConfigurationException ex)
//		{
//			throw new XmlReadException("read xml file " + filePath + "cause exception .", ex);
//		}
//		catch(IOException ex)
//		{
//			throw new XmlReadException("read xml file " + filePath + "cause exception .", ex);
//		}
//		catch(SAXException ex)
//		{
//			throw new XmlReadException("read xml file " + filePath + "cause exception .", ex);
//		}
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(new File(filePath));
		return new XmlElement(doc.getDocumentElement());
	}
	
	public String getName()
	{
		return this.element.getNodeName();
	}
	
	public XmlElement getChildByName(String name)
	{
		NodeList nodelst = this.element.getChildNodes();
		int len = nodelst.getLength();
		for(int i = 0; i < len; ++i)
		{
			Node node = nodelst.item(i);
			if (node.getNodeType() == Element.ELEMENT_NODE)
			{
				Element element = (Element)node;
				if (element.getNodeName().equals(name))
					return new XmlElement(element);	
			}
		}
		return null;
	}
	
	public List<XmlElement> getChildrenByName(String name)
	{
		//System.out.println("getChildrenByName(" + name + ")");
		List<XmlElement> lst = new ArrayList<>();
		NodeList nodelst = this.element.getChildNodes();
		int len = nodelst.getLength();
		for(int i = 0; i < len; ++i)
		{
			Node node = nodelst.item(i);
			if (node.getNodeType() == Element.ELEMENT_NODE)
			{
				Element element = (Element)node;
				if (element.getNodeName().equals(name))
					lst.add(new XmlElement(element));
			}
		}
		return lst;
	}
	
	public String getCDATASection()
	{
		StringBuilder sb = new StringBuilder();
		NodeList nodelst = this.element.getChildNodes();
		int len = nodelst.getLength();
		for(int i = 0; i < len; ++i)
		{
			Node node = nodelst.item(i);
			if (node.getNodeType() == Element.CDATA_SECTION_NODE)
			{
				CDATASection cdata = (CDATASection)node;
				sb.append(cdata.getData());
			}
		}
		return sb.toString();
	}
	
	public String getText()
	{
		StringBuilder sb = new StringBuilder();
		NodeList nodelst = this.element.getChildNodes();
		int len = nodelst.getLength();
		for(int i = 0; i < len; ++i)
		{
			Node node = nodelst.item(i);
			if (node instanceof Text)
			{
				Text txt = (Text)node;
				sb.append(txt.getData());
			}
		}
		return sb.toString();
	}
	
	public String getChildText(String name) throws XmlNodeNotFoundException
	{
		XmlElement element = getChildByName(name);
		if (element == null)
			throw new XmlNodeNotFoundException("no <" + name + "> element be found in all <" + this.element.getNodeName() + "> element's children !");
		return element.getText();
	}
	
	public String getChildText(String name, String defaultValue)
	{
		try
		{
			return getChildText(name);
		}
		catch(XmlNodeNotFoundException ex)
		{
			return defaultValue;
		}
	}
	
	public String getChildAttribute(String name, String attrname)
	{
		NodeList nodelst = this.element.getChildNodes();
		int len = nodelst.getLength();
		for(int i = 0; i < len; ++i)
		{
			Node node = nodelst.item(i);
			if (node.getNodeType() == Element.ELEMENT_NODE)
			{
				Element element = (Element)node;
				if (element.getNodeName().equals(name))
					if (element.hasAttribute(attrname))
						return element.getAttribute(attrname);
			}
		}
		return null;
	}
	
	public boolean hasAttribute(String name)
	{
		return this.element.hasAttribute(name);
	}
	
	public String getAttribute(String name) throws XmlNodeNotFoundException
	{
		if (!this.element.hasAttribute(name))
			throw new XmlNodeNotFoundException("no " + name + " attribute be found in <" + element.getNodeName() + "> element");
		String value = this.element.getAttribute(name);
		return value;
	}
	
	public String getStringAttribute(String name) throws XmlNodeNotFoundException
	{
		String value = getAttribute(name);
		return value;
	}
	
	public String getStringAttribute(String name, String defaultValue) throws XmlReadException
	{
		try
		{
			return getStringAttribute(name);
		}
		catch(XmlNodeNotFoundException ex)
		{
			return defaultValue;
		}
	}
	
	public int getIntegerAttribute(String name) throws XmlNodeNotFoundException, XmlReadException
	{
		String value = getAttribute(name);
		try
		{
			return Integer.parseInt(value);
		}   
		catch(NumberFormatException ex)
		{
			throw new XmlReadException(name + " attribute(" + value + ") in <" + element.getNodeName() + "> element has invalid integer format", ex);
		}
	}
	
	public int getIntegerAttribute(String name, int defaultValue) throws XmlReadException
	{
		try
		{
			return getIntegerAttribute(name);
		}
		catch(XmlNodeNotFoundException ex)
		{
			return defaultValue;
		}
	}
	
	public float getFloatAttribute(String name) throws XmlNodeNotFoundException, XmlReadException
	{
		String value = this.getAttribute(name);
		try
		{
			return Float.parseFloat(value);
		}   
		catch(NumberFormatException ex)
		{
			throw new XmlReadException(name + " attribute(" + value + ") in <" + element.getNodeName() + "> element has invalid float format", ex);
		}
	}
	
	public float getFloatAttribute(String name, float defaultValue) throws XmlReadException
	{
		try
		{
			return getFloatAttribute(name);
		}
		catch(XmlNodeNotFoundException ex)
		{
			return defaultValue;
		}
	}
	
	public boolean getBooleanAttribute(String name) throws XmlNodeNotFoundException, XmlReadException
	{
		String value = getAttribute(name);
		String s = value.trim().toLowerCase();
		return s.equals("y") || s.equals("t") || s.equals("true") || s.equals("yes");  
	}
	
	public boolean getBooleanAttribute(String name, boolean defaultValue) throws XmlReadException
	{
		try
		{
			return getBooleanAttribute(name);
		}
		catch(XmlNodeNotFoundException ex)
		{
			return defaultValue;
		}
	}
	
	public int getTimeAttribute(String name) throws XmlNodeNotFoundException, XmlReadException
	{
		String value = getAttribute(name);
		try
		{
			return GameTime.parseDateTime(value);
		}
		catch (java.text.ParseException ex)
		{
			throw new XmlReadException(name + " attribute(" + value + ") in <" + element.getNodeName() + "> element has invalid time format", ex);
		}
	}
	
	public int getTimeAttribute(String name, int defaultValue) throws XmlReadException
	{
		try
		{
			return getTimeAttribute(name);
		}
		catch(XmlNodeNotFoundException ex)
		{
			return defaultValue;
		}
	}
	
	public int getIntegerProperty(String name) throws XmlNodeNotFoundException, XmlReadException
	{
		String attrValue = getChildAttribute("property", name);
		if (attrValue == null)
			throw new XmlNodeNotFoundException("no <property> element with " + name + " attribute be found in all <" + element.getNodeName() + "> element's children !");
		try
		{
			return Integer.parseInt(attrValue);
		}   
		catch(NumberFormatException ex)
		{
			throw new XmlReadException("<" + element.getNodeName() + "> element's child <property> element with " + name + " attribute(" + attrValue + ") has invalid integer format", ex);
		}
	}
	
	public int getIntegerProperty(String name, int defaultValue) throws XmlReadException
	{
		try
		{
			return getIntegerProperty(name);
		}
		catch(XmlNodeNotFoundException ex)
		{
			return defaultValue;
		}
	}
	
	public float getFloatProperty(String name) throws XmlNodeNotFoundException, XmlReadException
	{
		String attrValue = getChildAttribute("property", name);
		if (attrValue == null)
			throw new XmlNodeNotFoundException("no <property> element with " + name + " attribute be found in all <" + element.getNodeName() + "> element's children !");
		try
		{
			return Float.parseFloat(attrValue);
		}   
		catch(NumberFormatException ex)
		{
			throw new XmlReadException("<" + element.getNodeName() + "> element's child <property> element with " + name + " attribute(" + attrValue + ") has invalid float format", ex);
		}
	}
	
	public float getFloatProperty(String name, float defaultValue) throws XmlReadException
	{
		try
		{
			return getFloatProperty(name);
		}
		catch(XmlNodeNotFoundException ex)
		{
			return defaultValue;
		}
	}
	
	public boolean getBooleanProperty(String name) throws XmlNodeNotFoundException, XmlReadException
	{
		String attrValue = getChildAttribute("property", name);
		if (attrValue == null)
			throw new XmlNodeNotFoundException("no <property> element with " + name + " attribute be found in all <" + element.getNodeName() + "> element's children !");
		String s = attrValue.trim().toLowerCase();
		return s.equals("y") || s.equals("t") || s.equals("true") || s.equals("yes");  
	}
	
	public boolean getBooleanProperty(String name, boolean defaultValue) throws XmlReadException
	{
		try
		{
			return getBooleanProperty(name);
		}
		catch(XmlNodeNotFoundException ex)
		{
			return defaultValue;
		}
	}
	
	public int getTimeProperty(String name) throws XmlNodeNotFoundException, XmlReadException
	{
		String attrValue = getChildAttribute("property", name);
		if (attrValue == null)
			throw new XmlNodeNotFoundException("no <property> element with " + name + " attribute be found in all <" + element.getNodeName() + "> element's children !");
		try
		{
			return GameTime.parseDateTime(attrValue);
		}
		catch (java.text.ParseException ex)
		{
			throw new XmlReadException("<" + element.getNodeName() + "> element's child <property> element with " + name + " attribute(" + attrValue + ") has invalid time format", ex);
		}
	}
	
	public int getTimeProperty(String name, int defaultValue) throws XmlReadException
	{
		try
		{
			return getTimeProperty(name);
		}
		catch(XmlNodeNotFoundException ex)
		{
			return defaultValue;
		}
	}
	
	public static void main(String[] args)
	{
		//org.apache.log4j.helpers.LogLog.setInternalDebugging(true);
//		org.apache.log4j.ConsoleAppender ca = new org.apache.log4j.ConsoleAppender();
//		ca.setName("AA");
//		ca.setWriter(new java.io.PrintWriter(System.out));
//		ca.setLayout(new org.apache.log4j.PatternLayout("%-5p %d{yyyy-MM-dd HH:mm:ss} %m%n"));
//		Logger.getRootLogger().addAppender(ca);
//		try
//		{
//
//		}
//		catch (Exception e)
//		{
//			Logger.getRootLogger().warn("exception :", e);
//		}
		
	}
}
