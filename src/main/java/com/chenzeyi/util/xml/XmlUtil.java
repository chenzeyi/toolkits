package com.chenzeyi.util.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.dom4j.Attribute;
import org.dom4j.CDATA;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


/**
 * > 转义 &gt;
 * <    &lt;
 * &    &amp;
 * "    &quot;
 * '    &apos;
 * 原生DOM解析XMl需要加载整个XML并创建对象树，可以修改XML内容；
 * 原生SAX解析速度快，但是不能修改内容
 * dom4j是个第三方的折中的方案
 * 
 * map转xml或者xml转map
 * 元素操作、缺少属性
 *
 */
public class XmlUtil {
	
	public Document LoadXmlByFile(File xmlfile) throws DocumentException {
		SAXReader reader = new SAXReader();
		return reader.read(xmlfile);
	}
	
	public Document LoadXmlByFilePath(String xmlfile) throws DocumentException, FileNotFoundException {
		return this.LoadXmlByFile(new File(xmlfile));
	}
	
	/**
	 * 
	 * @param xmlstr
	 * @return
	 * @throws DocumentException
	 * @throws FileNotFoundException
	 */
	public Document LoadXmlWithXmlStr(String xmlstr) throws DocumentException {
		return DocumentHelper.parseText(xmlstr);
	}
	
	/**
	 * 
	 * @param xmlstr
	 * @return
	 * @throws DocumentException
	 * @throws UnsupportedEncodingException
	 */
	public Document LoadXmlWithXmlStr2(String xmlstr) throws DocumentException, UnsupportedEncodingException {
		SAXReader reader = new SAXReader();
		return reader.read(new ByteArrayInputStream(xmlstr.getBytes("UTF-8")));
	}
	
	/**
	 * 
	 * @param ins
	 * @return
	 * @throws DocumentException
	 */
	public Document LoadXmlFromInputstream(InputStream ins) throws DocumentException {
		SAXReader reader = new SAXReader();
		return reader.read(ins);
	}
	
	public Map<String, Object> getAllElements(String xmlfile) throws DocumentException, FileNotFoundException {
		return getElements(this.LoadXmlByFilePath(xmlfile).getRootElement());
	}

	public Map<String, Object> getAllElements(File xmlfile) throws DocumentException {
		return getElements(this.LoadXmlByFile(xmlfile).getRootElement());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> getElements(Element parentelement) {
		Map<String, Object> elementsMap = new HashMap<String, Object>();
		List<Element> childrenElement = parentelement.elements();
		if (childrenElement == null)
			return elementsMap;
		for (Element element : childrenElement) {
			// 如果当前节点的子节点为空时，则向map中添加节点
			if (element.elements().isEmpty()) {
				// 如果返回的Map中已经包含了当前节点，分为两种情况
				// 1.如果value类型是List，则向list中增加当前节点的text
				// 2.如果value类型不是List，也就是单值，则将value的值塞入新增的List中，再将当前节点的text也塞入其中
				if (elementsMap.containsKey(element.getName())) {
					if (elementsMap.get(element.getName()) instanceof List) {
						((List) elementsMap.get(element.getName())).add(!"NULL".equals(element.getText()) ? element.getText() : null);
					} else {
						List elementList = new ArrayList();
						elementList.add(elementsMap.get(element.getName()));
						elementList.add(!"NULL".equals(element.getText()) ? element.getText() : null);
						elementsMap.put(element.getName(), elementList);
					}
				} else {
					elementsMap.put(element.getName(), !"NULL".equals(element.getText()) ? element.getText() : null);
				}
			} else {
				// 说明类似如上，不同点是取的当前节点的不是text，而是递归出来的Map
				if (elementsMap.containsKey(element.getName())) {
					if (elementsMap.get(element.getName()) instanceof List) {
						((List) elementsMap.get(element.getName())).add(getElements(element));
					} else {
						List list = new ArrayList();
						list.add(elementsMap.get(element.getName()));
						list.add(getElements(element));
						elementsMap.put(element.getName(), list);
					}
				} else {
					elementsMap.put(element.getName(), getElements(element));
				}
			}
		}
		return elementsMap;
	}

	public String getXmlDocStr(Map<String, Object> elementMap, String rootName) {
		return this.getXmlDocStr(elementMap, rootName, "UTF-8");
	}

	public String getXmlDocStr(Map<String, Object> elementMap, String rootName, String charaset) {
		Document doc = DocumentHelper.createDocument();
		doc.setXMLEncoding(charaset);
		doc.addElement(rootName);
		doc.addComment("XmlUtil generated");
		this.addElements(doc.getRootElement(), elementMap);
		return doc.asXML();
	}

	/**
	 * @param rootElement
	 *            根节点
	 * @param map
	 *            节点信息
	 */
	public void addElements(Element rootElement, Map<String, Object> map) {
		this.addElementByMap(rootElement, map);
	}

	/**
	 * @param parent
	 *            父节点
	 * @param map
	 *            当前节点信息
	 */
	private void addElementByMap(Element parent, Map<String, Object> map) {
		for (String key : map.keySet()) {
			this.addElementByString(parent, key, map.get(key));
		}
	}

	/**
	 * @param parent
	 *            父节点
	 * @param listNode
	 *            当前节点名
	 * @param value
	 *            当前节点的LIST信息
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void addElementByList(Element parent, String listNode, List<Object> value) {
		int size = value.size();
		if (size == 0) {
			Element node = parent.addElement(listNode);
			node.setText("");
			return;
		}
		for (int i = 0; i < size; i++) {
			Element node = parent.addElement(listNode);
			Object nodeValue = value.get(i);
			// List 中只能有String和Map类型，再有List类型就没有意义了
			// 所以这里只分析了两种类型
			if (nodeValue instanceof String) {
				node.setText((String) (nodeValue == null ? "NULL" : nodeValue));
			} else if (nodeValue instanceof Map) {
				addElementByMap(node, (Map) nodeValue);
			}
		}
	}

	/**
	 * @param parent
	 *            父节点
	 * @param nodePath
	 *            节点名信息 如：head/toll/value
	 * @param value
	 *            节点值
	 */
	@SuppressWarnings("unchecked")
	private void addElementByString(Element parent, String nodePath, Object value) {
		String[] nodes = nodePath.split("/");
		int size = nodes.length;
		for (int i = 0; i < size - 1; i++) {
			if (parent.element(nodes[i]) == null) {
				parent = parent.addElement(nodes[i]);
			} else {
				parent = parent.element(nodes[i]);
			}
		}
		
		if (value instanceof List) {
			this.addElementByList(parent, nodes[size - 1], (List<Object>) value);
		} else {
			if (parent.element(nodes[size - 1]) == null) {
				parent = parent.addElement(nodes[size - 1]);
			} else {
				parent = parent.element(nodes[size - 1]);
			}
			if (value instanceof String) {
				parent.setText((String) value);
			} else if (value instanceof CDATA) {
				parent.add((CDATA) value);
			} else if (value instanceof Map) {
				this.addElementByMap(parent, (Map<String,Object>) value);
			} else if (value == null) {
				parent.setText("NULL");
			} else {
				parent.setText(value.toString());
			}
		}
	}
	@SuppressWarnings("unchecked")
	public Map<String,String> getAttributes(Element element){
		Map<String,String> attributes = new HashMap<String,String>();
		Iterator<Attribute> iterator = element.attributeIterator();
		while(iterator.hasNext()){
			Attribute attribute = iterator.next();
			attributes.put(attribute.getName(), attribute.getValue());
		}
		return attributes;
	}
	
	public List<Element> getNodesByXpath(String xmlFile,String xPath) throws FileNotFoundException, DocumentException{
		Document doc = this.LoadXmlByFilePath(xmlFile);
		return this.getNodesByXpath(doc,xPath);
	}
	
	@SuppressWarnings("unchecked")
	public List<Element> getNodesByXpath(Document doc,String xPath){
		List<Element> elements = new ArrayList<Element>();
		elements = doc.selectNodes(xPath);
		return elements;
	}
	/**
	 * 如果有多个只返回第一个
	 * @param xmlFile
	 * @param xPath
	 * @return
	 * @throws DocumentException 
	 * @throws FileNotFoundException 
	 */
	public Node getSingleNodeByXpath(String xmlFile,String xPath) throws FileNotFoundException, DocumentException{
		Document doc = this.LoadXmlByFilePath(xmlFile);
		return 	doc.selectSingleNode(xPath);
	}
	/**
	 * 如果有多个只返回第一个
	 * @param doc
	 * @param xPath
	 * @return
	 */
	public Node getSingleNodeByXpath(Document doc,String xPath){
		return 	doc.selectSingleNode(xPath);
	}
	/**
	 * 格式化|去除空格之类的
	 * @param doc
	 * @param desXml
	 * @throws DocumentException
	 * @throws IOException
	 */
	public void formateXml(Document doc,String desXml) throws DocumentException, IOException{
		FileWriter fw = new FileWriter(desXml);
		OutputFormat of = OutputFormat.createPrettyPrint();
		XMLWriter xw = new XMLWriter(fw, of);
		xw.write(doc);
		xw.flush();
		xw.close();
	}
	/**
	 * 格式化|去除空格之类的
	 * @param sourceXml
	 * @param desXml
	 * @throws DocumentException
	 * @throws IOException
	 */
	public void formateXml(String sourceXml,String desXml) throws DocumentException, IOException{
		Document doc = this.LoadXmlByFilePath(sourceXml);
		FileWriter fw = new FileWriter(desXml);
		OutputFormat of = OutputFormat.createPrettyPrint();
		XMLWriter xw = new XMLWriter(fw, of);
		xw.write(doc);
		xw.flush();
		xw.close();
	}
	/**
	 * 改变单个元素的内容
	 * 多个则只改变第一个
	 * @param text
	 * @param xPath
	 * @throws DocumentException 
	 * @throws IOException 
	 */
	public void modifyElementTextByXpath(String xmlFile,String text,String xPath) throws DocumentException, IOException{
		Document doc = this.LoadXmlByFilePath(xmlFile);
		Node node = this.getSingleNodeByXpath(doc, xPath);
		node.setText(text);
		this.formateXml(doc, xmlFile);
	}
	/**
	 * 改变单个元素的内容
	 * 多个则只改变第一个
	 * @param text
	 * @param xPath
	 * @throws DocumentException 
	 * @throws IOException 
	 */
	public void addElementTextByXpath(String xmlFile,String nodename,String nodetext,String xPath) throws DocumentException, IOException{
		Document doc = this.LoadXmlByFilePath(xmlFile);
		Element element =(Element)this.getSingleNodeByXpath(doc, xPath);
		Element newElement = element.addElement(nodename);
		newElement.setText(nodetext);
		this.formateXml(doc, xmlFile);
	}
	/**
	 * 改变单个元素的内容
	 * 多个则只改变第一个
	 * @param text
	 * @param xPath
	 * @throws DocumentException 
	 * @throws IOException 
	 */
	public void removeElementTextByXpath(String xmlFile,String parentNodeXpath,String childNodexPath) throws DocumentException, IOException{
		Document doc = this.LoadXmlByFilePath(xmlFile);
		Element parentElement =(Element)this.getSingleNodeByXpath(doc, parentNodeXpath);
		Element childElement =(Element)this.getSingleNodeByXpath(doc, childNodexPath);
		parentElement.remove(childElement);
		this.formateXml(doc, xmlFile);
	}
	
	/**
	 * 生成CDATA字段
	 * @param content
	 * @return
	 */
	public CDATA CreateCDATA(String content){
		return DocumentHelper.createCDATA(content);
	}
}
