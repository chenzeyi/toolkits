package com.chenzeyi.util.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;

public class Demo {
	public static void main(String[] args) {
		try {
			XmlUtil xml = new XmlUtil();
//		boolean s = new XmlValidater().validateXml("file/xmlWithSysDtd.xml", "conf/XmlDtd.dtd");
//		boolean ss = new XmlValidater().validateXml("file/xmlWithSysXsd.xml", "conf/XmlXsd.xsd");
//		boolean sss = new XmlTransformer().transformXml("file/xmlWithXslt.xml", "conf/xmlXslt.xsl", "file/xmlToHtml.html");
//		System.out.println(s);
//		System.out.println(ss);
//		System.out.println(sss);
		Document doc = xml.LoadXmlByFile(new File("file/xml4Test.xml"));
//		List<Element> ss = xml.getNodesByXpath(doc, "//Computer");
//		for(Element e:ss){
//			System.out.println(e.getName()+"|"+e.getTextTrim());
//		}
//		Node node = xml.getSingleNodeByXpath(doc, "//Computer/Name");
//		System.out.println(node.getName()+"|"+node.getText().trim());
//		xml.formateXml("file/xml4Test.xml","file/xxxx.xml");
//		xml.modifyElementTextByXpath("file/xml4Test.xml", "JJJJJ", "//Computer/Name");
		
//		Map sss = xml.getAllElements("E://download.txt");
		List<Element> els = xml.getNodesByXpath("E://download.txt","root/BatchBean/document_Objects/BatchFileBean/files/FileBean");
		for(Element el:els){
			System.out.println("||"+el.getName());
			System.out.println(xml.getAttributes(el));
		}
		
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
}
