package com.chenzeyi.util.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.dom4j.util.XMLErrorHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
/**
 * 2016.03.15 23:51
 * 校验文件格式
 * @author zeyi.chen
 *
 */
public class XmlValidater {
	
	/**
	 * 根据校验文件后缀名来选择不同的校验方式
	 * @param xmlFile
	 * @param validateFile
	 * @return
	 */
	public boolean validateXml(String xmlFile,String validateFile){
		if(validateFile.endsWith(".dtd")){
			return this.validateXmlByDtd(xmlFile, validateFile);
		}else if(validateFile.endsWith(".xsd")){
			return this.validateXmlByXsd(xmlFile,validateFile);
		}else{
			return false;
		}
	}
	/**
	 * 外部schema格式校验
	 * @param xmlFile
	 * @param schemaFile
	 * @return
	 */
	private boolean validateXmlByXsd(final String xmlFile,final String xsdFile){
		boolean isCorrect = false;
		SAXReader reader = new SAXReader(true);
		try {
			reader.setFeature("http://xml.org/sax/features/validation", true);
			reader.setFeature("http://apache.org/xml/features/validation/schema", true);
			reader.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
			reader.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", xsdFile);
			isCorrect = validate(xmlFile, reader);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isCorrect;
	}
	/**
	 * 外部DTD格式校验
	 * @param xmlFile
	 * @param dtdFile
	 * @return
	 */
	public boolean validateXmlByDtd(final String xmlFile,final String dtdFile){
		boolean isCorrect = false;
		EntityResolver resover = new EntityResolver() {
			
			public InputSource resolveEntity(String publicId, String systemId)
					throws SAXException, IOException {
				// TODO Auto-generated method stub
				InputSource iso = new InputSource(new FileInputStream(dtdFile));
				iso.setPublicId(publicId);
				iso.setSystemId(systemId);
				return iso;
			}
		};
		SAXReader reader =new SAXReader(true);
		reader.setEntityResolver(resover);
		isCorrect = validate(xmlFile,reader);
		return isCorrect;
	}
	/**
	 * 校验
	 * @param xmlFile
	 * @param reader
	 * @return
	 */
	private boolean validate(String xmlFile,SAXReader reader){
		boolean result = false;
		XMLErrorHandler handler = new XMLErrorHandler();
		reader.setErrorHandler(handler);
		File xml = new File(xmlFile);
		if(xml.exists()&&xml.isFile()){
			try {
				reader.read(new InputStreamReader(new FileInputStream(xml), "UTF-8"));
				result = true;
				if(handler.getErrors().hasContent()) {
					result = false;
					throw new Exception(handler.getErrors().asXML());
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
}
