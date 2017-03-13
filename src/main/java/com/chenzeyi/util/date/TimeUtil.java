package com.chenzeyi.util.date;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
	public static final int YEAR = Calendar.YEAR;
	public static final int MONTH = Calendar.MONTH;
	public static final int DAY = Calendar.DATE;
	public static final int HOUR = Calendar.HOUR_OF_DAY;
	public static final int MINUTE = Calendar.MINUTE;
	public static final int SECOND = Calendar.SECOND;
	public static final int DAY_OF_MONTH = Calendar.DAY_OF_MONTH;
	public static final int DAY_OF_WEEK = Calendar.DAY_OF_WEEK;

	public static String dateTOstring(Date date) throws Exception {
		return dateTOstring(date, "yyyy-MM-dd HH:mm:ss");
	}

	public static String dateTOstring(Date date, String format) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	public static Date stringTOdate(String str, String format) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.parse(str);
	}

	public static Date longTOdate(long time) throws Exception {
		return new Date(time);
	}
	
	public static String longTOstring(long time) throws Exception {
		return longTOstring(time, "yyyy-MM-dd HH:mm:ss");
	}
	
	public static String longTOstring(long time, String format) throws Exception {
		Date date = longTOdate(time);
		return new SimpleDateFormat(format).format(date);
	}

	public static long stringTOlong(String str) throws Exception {
		return stringTOlong(str, "yyyy-MM-dd HH:mm:ss");
	}

	public static long stringTOlong(String str, String format) throws Exception {
		Date date = stringTOdate(str, format);
		if (date == null)
			return -1;
		return date.getTime();
	}
	
	public static Date setDate(Date date, int num, int type) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(type, num);
		return c.getTime();
	}
	
	public static Date forward(Date date, int num, int type) throws Exception {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(type, c.get(type) + num);
		return c.getTime();
	}

	public static Date back(Date date, int num, int type) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(type, c.get(type) - num);
		return c.getTime();
	}
	
	/**
	 * sourDate 大于或者等于 toBeComparedDate
	 * @param sourDate
	 * @param toBeComparedDate
	 * @return
	 */
	public static boolean afterOrEqual(Date sourDate,Date toBeComparedDate ){
		int result = sourDate.compareTo(toBeComparedDate);
		if(0==result||1==result){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * sourDate 小于或者等于 toBeComparedDate
	 * @param sourDate
	 * @param toBeComparedDate
	 * @return
	 */
	public static boolean beforeOrEqual(Date sourDate,Date toBeComparedDate){
		int result = sourDate.compareTo(toBeComparedDate);
		if(0==result||-1 ==result){
			return true;
		}else{
			return false;
		}
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(stringTOlong("2016-05-25 10:00:00"));
		System.out.println(new Date().getTime());
	}
}
