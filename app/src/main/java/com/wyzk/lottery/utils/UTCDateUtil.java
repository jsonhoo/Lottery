package com.wyzk.lottery.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日期类
 * copyright Copyright (c) 2010<br>
 * @version 2.0.0.0
 */
public class UTCDateUtil
{
	/** 日期格式：yyyy-MM-dd */
	public static final String FMT_DATE = "yyyy-MM-dd";
	/** 日期格式：HH:mm:ss */
	public static final String FMT_TIME = "HH:mm:ss";
	/** 日期格式：HH:mm:ss.SSS */
	public static final String FMT_TIMES = "HH:mm:ss.SSS";
	/** 日期格式：yyyy-MM-dd HH:mm:ss */
	public static final String FMT_DATETIME = "yyyy-MM-dd HH:mm:ss";
	/** 日期格式：yyyy-MM-dd HH:mm:ss.SSS */
	public static final String FMT_DATETIMES = "yyyy-MM-dd HH:mm:ss.SSS";
	
	/** 日期格式：yyyyMMdd */
	public static final String FMT_S_DATE = "yyyyMMdd";
	/** 日期格式：HHmmss */
	public static final String FMT_S_TIME = "HHmmss";
	/** 日期格式：HHmmssSSS */
	public static final String FMT_S_TIMES = "HHmmssSSS";
	/** 日期格式：yyyyMMddHHmmss */
	public static final String FMT_S_DATETIME = "yyyyMMddHHmmss";
	/** 日期格式：yyyyMMddHHmmssSSS */
	public static final String FMT_S_DATETIMES = "yyyyMMddHHmmssSSS";
	
	/**
	 * 标准全球时区
	 */
	public static final TimeZone UTC = TimeZone.getTimeZone("Etc/GMT");
	
    /**
     * 解析日期
     * @param date 日期字符串
     * @param dateFormat 日期格式化模式
     * @return Date
     */
    public static Date parse(String date, String dateFormat)
    {
    	try
		{
			return new SimpleDateFormat(dateFormat).parse(date);
		}
		catch (ParseException e)
		{
			throw new IllegalArgumentException(e);
		}
    }

    /**
     * 解析日期
     * @param date 日期字符串
     * @param dateFormat 日期格式化模式
     * @param timeZone 时区
     * @return Date
     */
    public static Date parse(String date, String dateFormat, TimeZone timeZone)
    {
    	try
		{
    		SimpleDateFormat df = new SimpleDateFormat(dateFormat);
    		df.setTimeZone(timeZone);
			return df.parse(date);
		}
		catch (ParseException e)
		{
			throw new IllegalArgumentException(e);
		}
    }
    
    /**
     * 格式化日期
     * @param date 日期
     * @param dateFormat 日期格式化模式
     * @return 日期字符串
     */
    public static String format(Date date, String dateFormat)
    {
    	return new SimpleDateFormat(dateFormat).format(date);
    }

    /**
     * 格式化日期
     * @param date 日期
     * @param dateFormat 日期格式化模式
     * @param timeZone 时区
     * @return 日期字符串
     */
    public static String format(Date date, String dateFormat, TimeZone timeZone)
    {
    	SimpleDateFormat df = new SimpleDateFormat(dateFormat);
		df.setTimeZone(timeZone);
    	return df.format(date);
    }

    /**
     * 格式化日期
     * @param millis 日期表示的毫秒数
     * @param dateFormat 日期格式化模式
     * @return 日期字符串
     */
    public static String format(long millis, String dateFormat)
    {
    	return new SimpleDateFormat(dateFormat).format(new Date(millis));
    }

    /**
     * 格式化日期
     * @param millis 日期表示的毫秒数
     * @param dateFormat 日期格式化模式
     * @param timeZone 时区
     * @return 日期字符串
     */
    public static String format(long millis, String dateFormat, TimeZone timeZone)
    {
    	SimpleDateFormat df = new SimpleDateFormat(dateFormat);
		df.setTimeZone(timeZone);
    	return df.format(new Date(millis));
    }
    
    /**
     * 获得标准时区的日期
     * @return UTC日期
     */
    public static Date getUtcDate()
    {
    	Calendar cal = Calendar.getInstance();
    	cal.add(Calendar.MILLISECOND, -TimeZone.getDefault().getRawOffset());
    	return cal.getTime();
    }

    /**
     * 获得年份
     * @param date 日期
     * @return 年份
     */
    public static int getYear(Date date)
    {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
    	return cal.get(Calendar.YEAR);
    }
    
    /**
     * 获得月份 [1,12]
     * @param date 日期
     * @return 月份
     */
    public static int getMonth(Date date)
    {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
    	return cal.get(Calendar.MONTH) + 1;
    }
    
    /**
     * 获得日[1,31]
     * @param date 日期
     * @return 日
     */
    public static int getDay(Date date)
    {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
    	return cal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获得星期[1,7]
     * @param date 日期
     * @return 星期，1表示星期一，2表示星期二......7表示星期日
     */
    public static int getWeek(Date date)
    {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
    	int week = cal.get(Calendar.DAY_OF_WEEK);
    	return week == 1 ? 7 : week - 1;
    }
    
    /**
     * 获取24进制小时
     * @param date 日期
     * @return 24进制小时
     */
    public static int getHour(Date date)
    {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
    	return cal.get(Calendar.HOUR_OF_DAY);
    }
    
    /**
     * 获取分钟
     * @param date 日期
     * @return 分钟
     */
    public static int getMinute(Date date)
    {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
    	return cal.get(Calendar.MINUTE);
    }

    /**
     * 获取秒
     * @param date 日期
     * @return 秒
     */
    public static int getSecond(Date date)
    {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
    	return cal.get(Calendar.SECOND);
    }

    /**
     * 获取毫秒
     * @param date 日期
     * @return 毫秒
     */
    public static int getMillis(Date date)
    {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
    	return cal.get(Calendar.MILLISECOND);
    }
    
    /**
     * 获得月末日期
     * @param date 日期
     * @return 月末日期，时分秒毫秒均为零
     */
    public static Date getMonthEnd(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.MONTH, 1);
        c.add(Calendar.DAY_OF_MONTH, -1);
        return c.getTime();
    }

    /**
     * 获得所在月份天数
     * @param date 日期
     * @return 所在月份天数
     */
    public static int getMonthDays(Date date)
    {
    	Calendar c = Calendar.getInstance();
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        return getMonthDays(year, month);
    }

    /**
     * 获得某年某月有多少天
     * @param year 年
     * @param month 月
     * @return 某年某月的天数
     */
    public static int getMonthDays(int year, int month)
    {
        int days = 31;
        switch (month)
        {
            case 4: case 6: case 9: case 11: days = 30; break;
            case 2: days = isLeapYear(year) ? 29 : 28; break;
        }
        return days;
    }

    /**
     * 获得两个日期之间相差的天数
     * @param startDate 起始日期
     * @param endDate 结束日期
     * @return 天数
     */
    public static int getDaysBetween(Date startDate, Date endDate)
    {
    	long start = startDate.getTime()/1000/60/60/24;
    	long end = endDate.getTime()/1000/60/60/24;
    	return (int) (start - end);
    }

    /**
     * 获得n秒对应的毫秒数
     * @param n 秒
     * @return 毫秒数
     */
    public static long getMillisOfSeconds(long n)
    {
        return (n * 1000);
    }
    
    /**
     * 获得n分钟对应的毫秒数
     * @param n 分钟
     * @return 毫秒数
     */
    public static long getMillisOfMinutes(long n)
    {
        return (n * 60 * 1000);
    }
    
    /**
     * 获得n小时对应的毫秒数
     * @param n 小时
     * @return 毫秒数
     */
    public static long getMillisOfHours(long n)
    {
        return (n * 60 * 60 * 1000);
    }
    
    /**
     * 获得n天对应的毫秒数
     * @param n 天
     * @return 毫秒数
     */
    public static long getMillisOfDays(long n)
    {
        return (n * 24 * 60 * 60 * 1000);
    }
    
    /**
     * 获得n周对应的毫秒数
     * @param n 周
     * @return 毫秒数
     */
    public static long getMillisOfWeeks(long n)
    {
        return (n * 7 * 24 * 60 * 60 * 1000);
    }
    
    /**
     * 匹配带有单位的时间字符串的正则表达式
     */
    private static Pattern timePattern = Pattern.compile("^(\\d+)(ms|s|m|h|d|w)$");
    
    /**
     * 根据带有单位的时间格式，获得其毫秒数。
     * @param timeString 带有单位的时间格式 （一个整数+单位(毫秒ms, 秒s, 分m, 小时h, 天d, 周w)），如：10小时为 "10h"
     * @return 毫秒数
     */
    public static long getMillis(String timeString)
    {
    	Matcher m = timePattern.matcher(timeString);
    	if (!m.find())
    	{
    		throw new IllegalArgumentException("无法识别的时间格式：" + timeString);
    	}
    	
    	long num = Long.parseLong(m.group(1));
    	String unit = m.group(2);
    	if (unit.equals("ms"))
    	{
    		return num;
    	}
    	else if (unit.equals("s"))
    	{
    		return getMillisOfSeconds(num);
    	}
    	else if (unit.equals("m"))
    	{
    		return getMillisOfMinutes(num);
    	}
    	else if (unit.equals("h"))
    	{
    		return getMillisOfHours(num);
    	}
    	else if (unit.equals("d"))
    	{
    		return getMillisOfDays(num);
    	}
    	else if (unit.equals("w"))
    	{
    		return getMillisOfWeeks(num);
    	}
    	
    	throw new IllegalArgumentException("无法识别的时间单位：" + timeString);
    }

    /**
     * 获得两个日期之间相差的毫秒数
     * @param date1 日期
     * @param date2 日期
     * @return 相差毫秒数
     */
    public static long diffMillis(Date date1, Date date2)
    {
    	return Math.abs(date1.getTime() - date2.getTime());
    }
    
    /**
     * 获得两个日期之间相差的秒数
     * @param date1 日期
     * @param date2 日期
     * @return 相差秒数
     */
    public static long diffSeconds(Date date1, Date date2)
    {
    	return Math.abs((date1.getTime() - date2.getTime()) / 1000);
    }
    
    /**
     * 获得两个日期之间相差的分钟数
     * @param date1 日期
     * @param date2 日期
     * @return 相差分钟数
     */
    public static long diffMinutes(Date date1, Date date2)
    {
    	return Math.abs((date1.getTime() - date2.getTime()) / 1000 / 60);
    }
    
    /**
     * 获得两个日期之间相差的小时数
     * @param date1 日期
     * @param date2 日期
     * @return 相差小时数
     */
    public static long diffHours(Date date1, Date date2)
    {
    	return Math.abs((date1.getTime() - date2.getTime()) / 1000 / 60 / 60);
    }
    
    /**
     * 获得两个日期之间相差的天数
     * @param date1 日期
     * @param date2 日期
     * @return 相差天数
     */
    public static long diffDays(Date date1, Date date2)
    {
    	return Math.abs((date1.getTime() - date2.getTime()) / 1000 / 60 / 60 / 24);
    }

    /**
     * 获得两个日期之间相差的周数
     * @param date1 日期
     * @param date2 日期
     * @return 相差周数
     */
    public static long diffWeeks(Date date1, Date date2)
    {
    	return Math.abs((date1.getTime() - date2.getTime()) / 1000 / 60 / 60 / 24 / 7);
    }
    
    /**
     * 将时间舍入为分钟，
     * 如：2013-7-13 15:18:3.123舍入后，则是 2013-7-13 15:18:0.0
     * @param date 被舍入的时间
     * @return 舍入后的时间
     */
    public static Date floorToMinute(Date date)
    {
    	return floorToMinute(date, 1);
    }

    /**
     * 将时间舍入为指定的N分钟的倍数，
     * 如：2013-7-13 15:18:3.123舍入为5分钟倍数后，则是 2013-7-13 15:15:0.0
     * @param date 被舍入的时间
     * @param nMinutes N分钟
     * @return 舍入后的时间
     */
    public static Date floorToMinute(Date date, int nMinutes)
    {
    	Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		int minutes = cal.get(Calendar.MINUTE);
		cal.add(Calendar.MINUTE, -(minutes % nMinutes));
		
		return cal.getTime();
    }

    /**
     * 将时间舍入为小时，
     * 如：2013-7-13 15:18:3.123舍入后，则是 2013-7-13 15:0:0.0
     * @param date 被舍入的时间
     * @return 舍入后的时间
     */
    public static Date floorToHour(Date date)
    {
    	return floorToHour(date, 1);
    }

    /**
     * 将时间舍入为指定的N小时的倍数，
     * 如：2013-7-13 15:18:3.123舍入为2小时倍数后，则是 2013-7-13 14:0:0.0
     * @param date 被舍入的时间
     * @param nHours N小时
     * @return 舍入后的时间
     */
    public static Date floorToHour(Date date, int nHours)
    {
    	Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		int hours = cal.get(Calendar.HOUR_OF_DAY);
		cal.add(Calendar.HOUR_OF_DAY, -(hours % nHours));
		
		return cal.getTime();
    }

    /**
     * 将时间舍入为所在天的开始日期，
     * 如：2013-7-13 15:18:3.123舍入后，则是 2013-7-13 0:0:0.0
     * @param date 被舍入的时间
     * @return 舍入后的时间
     */
    public static Date floorToDay(Date date)
    {
    	Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTime();
    }
    
    /**
     * 将时间舍入为所在周的开始日期，周一作为周的开始。
     * 如：2013-7-13 15:18:3.123舍入后，则是 2013-7-8 0:0:0.0
     * @param date 被舍入的时间
     * @return 舍入后的时间
     */
    public static Date floorToWeek(Date date)
    {
    	Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		int week = cal.get(Calendar.DAY_OF_WEEK);// 1 ~ 7
		
		if (week == 1)//周日
		{
			cal.add(Calendar.DAY_OF_WEEK, -6);
		}
		else
		{
			cal.add(Calendar.DAY_OF_WEEK, 2 - week);
		}
		
		return cal.getTime();
    }

    /**
     * 将时间舍入为所在月的开始日期，
     * 如：2013-7-13 15:18:3.123舍入后，则是 2013-7-1 0:0:0.0
     * @param date 被舍入的时间
     * @return 舍入后的时间
     */
    public static Date floorToMonth(Date date)
    {
    	Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTime();
    }

    /**
     * 将时间舍入为所在年的开始日期，
     * 如：2013-7-13 15:18:3.123舍入后，则是 2013-1-1 0:0:0.0
     * @param date 被舍入的时间
     * @return 舍入后的时间
     */
    public static Date floorToYear(Date date)
    {
    	Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTime();
    }

    /**
     * 将时间增入为分钟，
     * 如：2013-7-13 15:18:3.123增入后，则是 2013-7-13 15:19:0.0
     * @param date 被增入的时间
     * @return 增入后的时间
     */
    public static Date ceilToMinute(Date date)
    {
    	return ceilToMinute(date, 1);
    }

    /**
     * 将时间增入为指定的N分钟的倍数，
     * 如：2013-7-13 15:18:3.123增入为5分钟倍数后，则是 2013-7-13 15:20:0.0
     * @param date 被增入的时间
     * @param nMinutes N分钟
     * @return 增入后的时间
     */
    public static Date ceilToMinute(Date date, int nMinutes)
    {
    	Date floorDate = floorToMinute(date, nMinutes);
    	if (floorDate.equals(date))
    	{
    		return floorDate;
    	}
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(floorDate);
    	cal.add(Calendar.MINUTE, nMinutes);
		return cal.getTime();
    }

    /**
     * 将时间增入为小时，
     * 如：2013-7-13 15:18:3.123增入后，则是 2013-7-13 16:0:0.0
     * @param date 被增入的时间
     * @return 增入后的时间
     */
    public static Date ceilToHour(Date date)
    {
    	return ceilToHour(date, 1);
    }

    /**
     * 将时间增入为指定的N小时的倍数，
     * 如：2013-7-13 15:18:3.123增入为2小时倍数后，则是 2013-7-13 16:0:0.0
     * @param date 被增入的时间
     * @param nHours N小时
     * @return 增入后的时间
     */
    public static Date ceilToHour(Date date, int nHours)
    {
    	Date floorDate = floorToHour(date, nHours);
    	if (floorDate.equals(date))
    	{
    		return floorDate;
    	}
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(floorDate);
    	cal.add(Calendar.HOUR_OF_DAY, nHours);
		return cal.getTime();
    }

    /**
     * 将时间增入为天，
     * 如：2013-7-13 15:18:3.123增入后，则是 2013-7-14 0:0:0.0
     * @param date 被增入的时间
     * @return 增入后的时间
     */
    public static Date ceilToDay(Date date)
    {
    	Date floorDate = floorToDay(date);
    	if (floorDate.equals(date))
    	{
    		return floorDate;
    	}
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(floorDate);
    	cal.add(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
    }
    
    /**
     * 将时间增入为所在周末日期，周一作为周的开始。
     * 如：2013-7-11 15:18:3.123增入后，则是 2013-7-14 0:0:0.0
     * @param date 被增入的时间
     * @return 增入后的时间
     */
    public static Date ceilToWeek(Date date)
    {
    	Date floorDate = floorToWeek(date);
    	if (floorDate.equals(date))
    	{
    		return floorDate;
    	}
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(floorDate);
    	cal.add(Calendar.DAY_OF_MONTH, 7);
		return cal.getTime();
    }

    /**
     * 将时间增入为下个月的开始日期，如：2013-7-13 15:18:3.123增入后，则是 2013-8-1 0:0:0.0
     * @param date 被增入的时间
     * @return 增入后的时间
     */
    public static Date ceilToMonth(Date date)
    {
    	Date floorDate = floorToMonth(date);
    	if (floorDate.equals(date))
    	{
    		return floorDate;
    	}
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(floorDate);
    	cal.add(Calendar.MONTH, 1);
		return cal.getTime();
    }

    /**
     * 将时间增入为下一年的开始日期，如：2013-7-13 15:18:3.123增入后，则是 2014-1-1 0:0:0.0
     * @param date 被增入的时间
     * @return 增入后的时间
     */
    public static Date ceilToYear(Date date)
    {
    	Date floorDate = floorToYear(date);
    	if (floorDate.equals(date))
    	{
    		return floorDate;
    	}
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(floorDate);
    	cal.add(Calendar.YEAR, 1);
		return cal.getTime();
    }

    /**
     * 判断两个时间对象是否一样，可比较null情况和继承类情况。
     * @param date1 一个时间，可为null
     * @param date2 另一个时间，可为null
     * @return 同时为null时返回true，否则看其内部时间表示是否一样。
     */
    public static boolean isSame(Date date1, Date date2)
    {
    	if (date1 == null && date2 == null)
    	{
    		return true;
    	}
    	else if (date1 == null || date2 == null)
    	{
    		return false;
    	}
    	else
    	{
    		return date1.compareTo(date2) == 0;
    	}
    }

    /**
     * 判断两个时间对象比较情况，可比较null情况和继承类情况。
     * @param date1 一个时间，可为null
     * @param date2 另一个时间，可为null
     * @return 相等返回0（同为null表示相等），第一个时间大于第二个时间返回1，否则返回-1。
     */
    public static int compare(Date date1, Date date2)
    {
    	if (date1 == null && date2 == null)
    	{
    		return 0;
    	}
    	else if (date1 == null)
    	{
    		return -1;
    	}
    	else if (date2 == null)
    	{
    		return 1;
    	}
    	else
    	{
    		return date1.compareTo(date2);
    	}
    }
    
    /**
     * 判断给定的年份是否是闰年
     * @param year 年份
     * @return true，是闰年
     */
    public static boolean isLeapYear(int year)
    {
        return (year % 4 == 0 && year % 100 != 0) || (year % 100 == 0 && year % 400 == 0);
    }

    /**
     * 是否是闰年
     * @param s 字符串
     * @return true，是
     */
    public static boolean isLeapYear(String s)
    {
        int year = 0;
        try
        {
            year = Integer.parseInt(s);
        }
        catch (NumberFormatException e)
        {
            return false;
        }
        return isLeapYear(year);
    }

    /**
     * 是否是日期格式（yyyy-MM），包括对日期范围的验证。
     * @param s 字符串
     * @return true，是
     */
    public static boolean isLongYearMonth(String s)
    {
        return s.matches("^\\d{4}-(0[1-9]|1[0-2])$");
    }

    /**
     * 是否是日期格式（yyyy-MM-dd），包括对日期范围的验证。
     * @param s 字符串
     * @return true，是
     */
    public static boolean isLongDate(String s)
    {
        if (s.length() == 10)
        {
            String dateRegex = "\\d{4}-(((01|03|05|07|08|10|12)-(0[1-9]|[12]\\d|3[01]))|((04|06|09|11)-(0[1-9]|[12]\\d|30))|(02-";
            dateRegex += isLeapYear(s.substring(0, 4)) ? "(0[1-9]|[12]\\d)" : "(0[1-9]|1\\d|2[0-8])";
            dateRegex += "))";
            return s.matches(dateRegex);
        }
        return false;
    }

    /**
     * 是否是日期格式（yyyyMMdd）
     * @param s 字符串
     * @return true，是
     */
    public static boolean isShortDate(String s)
    {
        if (s.length() == 8)
        {
            String dateRegex = "\\d{4}(((01|03|05|07|08|10|12)(0[1-9]|[12]\\d|3[01]))|((04|06|09|11)(0[1-9]|[12]\\d|30))|(02";
            dateRegex += isLeapYear(s.substring(0, 4)) ? "(0[1-9]|[12]\\d)" : "(0[1-9]|1\\d|2[0-8])";
            dateRegex += "))";
            return s.matches(dateRegex);
        }
        return false;
    }

    /**
     * 是否是时间格式（HH:mm:ss），包括对时间范围的验证。
     * @param s 字符串
     * @return true，是
     */
    public static boolean isLongTime(String s)
    {
        return s.matches("^([01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d$");
    }

    /**
     * 是否是时间格式（HHmmss），包括对时间范围的验证。
     * @param s 字符串
     * @return true，是
     */
    public static boolean isShortTime(String s)
    {
        return s.matches("^([01]\\d|2[0-3])[0-5]\\d[0-5]\\d$");
    }
    
    /**
     * 是否是日期时间格式（yyyy-MM-dd HH:mm:ss），包括对日期范围的验证。
     * @param s 字符串
     * @return true，是
     */
    public static boolean isLongDateTime(String s)
    {
        return (s.length() == 19 && s.charAt(10) == ' ') ? (isLongDate(s.substring(0, 10)) && isLongTime(s.substring(11, 19))) : false;
    }

    /**
     * 是否是日期时间格式（yyyyMMddHHmmss）
     * @param s 字符串
     * @return true，是
     */
    public static boolean isShortDateTime(String s)
    {
        return (s.length() == 14) ? (isShortDate(s.substring(0, 8)) && isShortTime(s.substring(8, 14))) : false;
    }
    
    /**
     * Utc时间和本地时间的转换
     * @param utcDate
     * @return
     */
    public static Date getLocalDate(Date utcDate)
    {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(utcDate);
    	cal.add(Calendar.MILLISECOND, +TimeZone.getDefault().getRawOffset());
    	return cal.getTime();
    }

}

