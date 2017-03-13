package com.chenzeyi.util.xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * 利用xslt转换xml为html
 * @author zeyi.chen
 *
 */
public class XmlTransformer {
	
	public boolean transformXml(String xmlPath,String xslPath,String htmlpath){
		boolean transformedSucceed = false;
		try {
			TransformerFactory fac = TransformerFactory.newInstance();
//			Source xslSource = new StreamSource(xslPath);
//			Source xmlSource = new StreamSource(xmlPath);
			Source xslSource = new StreamSource(new InputStreamReader(new FileInputStream(xslPath), "UTF-8"));
			Source xmlSource = new StreamSource(new InputStreamReader(new FileInputStream(xmlPath), "UTF-8"));
			Result htmlResult = new StreamResult(htmlpath);
			
			Transformer transformer = fac.newTransformer(xslSource);
			transformer.transform(xmlSource, htmlResult);
			transformedSucceed = true;
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return transformedSucceed;
	}
}
