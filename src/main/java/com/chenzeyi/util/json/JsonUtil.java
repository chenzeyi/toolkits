package com.chenzeyi.util.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.CookieList;
import org.json.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

public class JsonUtil {
	
	private static JSONObject xmlToJson(String xmlStr) throws JSONException{
		JSONObject json = null;
		json = XML.toJSONObject(xmlStr);
		return json;
	}
	
	private static JSONObject mapToJson(Map<String,Object> map) throws JSONException{
		JSONObject json = null;
		json = new JSONObject(map);
		return json;
	}
	
	private static JSONObject beanToJson(Object obj){
		JSONObject json = null;
		json = new JSONObject(obj);
		return json;
	}
	
	private static JSONObject cookieToJson(String cookieStr) throws JSONException{
		JSONObject json = null;
		json = CookieList.toJSONObject(cookieStr);
		return json;
	}
	
	private static JSONObject httpToJson(String httpStr) throws JSONException{
		JSONObject json = null;
		json = HTTP.toJSONObject(httpStr);
		return json;
	}
	
	private static Map<String,Object> jsonToMap(JSONObject jsb) throws JSONException{
		Map<String,Object> jsm = new HashMap<String,Object>();
		Iterator<String> keys = jsb.keys();
		while(keys.hasNext()){
			String key = keys.next();
			Object value = jsb.get(key);
			jsm.put(key, value);
		}
		return jsm;
	}
	public static void main(String[] args) {
		try{
			JSONObject jsb = null;
			jsb = xmlToJson("<ROOT>root<HEAD id=\"h1\" >head</HEAD><BODY>body</BODY></ROOT>");
			System.out.println(jsb.toString());
			jsb = httpToJson("GET / HTTP/1.0\nAccept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*\nAccept-Language: en-us\nUser-Agent: Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; Win 9x 4.90; T312461; Q312461)\nHost: www.nokko.com\nConnection: keep-alive\nAccept-encoding: gzip, deflate\n");
			System.out.println(jsb.toString());
			jsb = cookieToJson("f%oo = b+l=ah  ; o;n%40e = t.wo");
			System.out.println(jsb.toString());
			Map a = new HashMap();
			List s = new ArrayList();
			s.add("s");
			Map b = new HashMap();
			b.put("b", "b");
			a.put("a", "a");
			a.put("b", b);
			s.add(b);
			a.put("s", s);
			jsb = mapToJson(a);
			System.out.println(jsb.toString());
			Map m = jsonToMap(jsb);
			System.out.println(m);
			Nation n = new Nation("USA","ENGLISH","NON");
			jsb = beanToJson(n);
			System.out.println(jsb.toString());
		}catch(Exception e){
			e.getStackTrace();
		}
	}

}
