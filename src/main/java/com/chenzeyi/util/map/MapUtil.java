package com.chenzeyi.util.map;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理多层Map取值
 * 
 * @author 董敏 时间：2011年6月3日 16:01:23
 */
public class MapUtil {
	@SuppressWarnings("rawtypes")
	public static Object getValueByStringKey(Map map, String keys) {
		if (map == null)
			return null;
		if (keys == null)
			return null;
		if ("/".equals(keys))
			return map;
		if (map.containsKey(keys)) {
			return map.get(keys);
		}
		String[] tmpkeys = keys.split("/");
		Object value = map.get(tmpkeys[0]);
		for (int i = 1; i < tmpkeys.length; i++) {
			if (value == null)
				return null;
			if (value instanceof Map) {
				value = ((Map) value).get(tmpkeys[i]);
				continue;
			}
			// 这种情况说明取的路径有多个值，取定第一个值
			if (value instanceof List) {
				value = ((List) value).get(0);
				continue;
			}
			if (value.getClass() == String.class)
				return value;
		}
		return value;
	}

	@SuppressWarnings({ "rawtypes" })
	public static Object removeValueByStringKey(Map map, String keys) {
		if (keys == null)
			return null;
		if (map == null)
			throw new NullPointerException();
		if ("/".equals(keys)) {
			map.clear();
			return map;
		}
		String[] tmpkeys = keys.split("/");
		Object temp = map;
		for (int i = 0, j = tmpkeys.length; i < j; i++) {
			if (i == j - 1) {
				return ((Map) temp).remove(tmpkeys[i]);
			}
			temp = ((Map) temp).get(tmpkeys[i]);
			if (!(temp instanceof Map)) {
				return null;
			}
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void putValue(Map map, String keypath, Object value) {
		if (map == null)
			return;
		if (keypath == null)
			return;
		if ("/".equals(keypath)) {
			if (value instanceof Map) {
				map.putAll((Map) value);
			} else if (value instanceof String) {
				map.put("VALUE", value);
			} else if (value instanceof List) {
				map.put("OBJECT", value);
			}
		} else {
			String[] keys = keypath.split("/");
			Object tmp = null;
			Map mp = map;
			for (int i = 0, size = keys.length; i < size; i++) {
				if (i == size - 1) {
					mp.put(keys[i], value);
					break;
				}
				tmp = mp.get(keys[i]);
				if (!(tmp instanceof Map)) {
					tmp = new HashMap();
					mp.put(keys[i], tmp);
				}
				mp = (Map) tmp;
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void putOrderedValue(LinkedHashMap map, String keypath, Object value) {// 人行接口需要内容字段排序，采用LKMAP
		if (map == null)
			return;
		if (keypath == null)
			return;
		if ("/".equals(keypath)) {
			if (value instanceof Map) {
				map.putAll((Map) value);
			} else if (value instanceof String) {
				map.put("VALUE", value);
			} else if (value instanceof List) {
				map.put("OBJECT", value);
			}
		} else {
			String[] keys = keypath.split("/");
			Object tmp = null;
			LinkedHashMap mp = map;
			for (int i = 0, size = keys.length; i < size; i++) {
				if (i == size - 1) {
					mp.put(keys[i], value);
					break;
				}
				tmp = mp.get(keys[i]);
				if (!(tmp instanceof Map)) {
					tmp = new LinkedHashMap();
					mp.put(keys[i], tmp);
				}
				mp = (LinkedHashMap) tmp;
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<Object> getListValue(String key, Map<String, Object> data) {
		Object object = getValueByStringKey(data, key);
		if (object == null)
			return null;
		if (object instanceof List)
			return (List<Object>) object;
		if (object instanceof Map) {
			List list = new ArrayList();
			list.add(object);
			return list;
		}
		if (object instanceof String) {
			List list = new ArrayList();
			list.add(object);
			return list;
		}
		return null;
	}

	/**
	 * 将对象转换成Map
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map objectToMap(Object object) throws Exception {
		// 获得对象的类型
		Class classType = object.getClass();
		// 获得对象的所有属性
		Field fields[] = classType.getDeclaredFields();
		Map map = new HashMap();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			String fieldName = field.getName();

			String firstLetter = fieldName.substring(0, 1).toUpperCase();
			// 获得和属性对应的getXXX()方法的名字
			String getMethodName = "get" + firstLetter + fieldName.substring(1);
			Method getMethod = classType.getMethod(getMethodName, new Class[] {});
			// 调用原对象的getXXX()方法
			Object value = getMethod.invoke(object, new Object[] {});
			// 调用复制对象的setXXX()方法
			fieldName.toUpperCase();
			map.put(fieldName, value);
		}
		return map;
	}
}
