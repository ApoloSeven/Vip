package com.feijian.utils;

import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Title: DateUtils
 * @Description:
 * @author: ApoloSeven
 * @version: 1.0
 */
public class DateUtils {

    public static final String FORMAT_ONE = "yyyy-MM-dd HH:mm:ss";

    public static final String FORMAT_TWO = "yyyy-MM-dd";


    /**
     * 增加时间单位：天
     *
     * @param day
     * @return
     */
    public static Date addDay(Date origin, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(origin);
        cal.add(Calendar.DATE, day);
        return cal.getTime();
    }

    /**
     * 增加时间单位：分钟
     *
     * @param minute
     * @return
     */
    public static String addMinute(int minute) {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_ONE);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, minute);
        return sdf.format(cal.getTime());
    }


    /**
     * 把Date转为String
     *
     * @param date
     * @param format
     * @return
     */
    public static String dateToString(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 把日期字符串转化成Date类型
     *
     * @param dateStr
     * @param format
     * @return
     */
    public static Date stringToDate(String dateStr, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取本月第一天
     *
     * @return
     */
    public static Date getFirstDayOfCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int dateIndex = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DATE, 1 - 1 * dateIndex);
        return ignoreTime(calendar.getTime());
    }

    /**
     * 获取下个月第一天
     *
     * @return
     */
    public static Date getFirstDayOfNextMonth() {
        Date date = getFirstDayOfCurrentMonth();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        return calendar.getTime();
    }


    /**
     * 忽略掉时分秒，只保留年月日
     *
     * @param date
     * @return
     */
    public static Date ignoreTime(Date date) {
        String dateStr = dateToString(date, FORMAT_ONE);
        return stringToDate(StringUtils.substringBefore(dateStr, " "), FORMAT_TWO);
    }

    public static Date ignoreTime(String dateStr) {
        return stringToDate(StringUtils.substringBefore(dateStr, " "), FORMAT_TWO);
    }


    /**
     * 计算一天中，两个时间相差几小时
     *
     * @param
     * @param
     * @return 10
     */
    public static int hourDiff(Date startTime, Date endTime) {
        long diffMillis = endTime.getTime() - startTime.getTime();
        long diffHour = diffMillis % 3600000 == 0 ? diffMillis / 3600000 : diffMillis / 3600000 + 1;
        return (int) diffHour;
    }

    /**
     * 计算两个时间点相差几天，首尾不是一天的算两天
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static int dayDiff(Date startTime, Date endTime) {
        Date startZero = ignoreTime(startTime);
        Date endZero = ignoreTime(endTime);
        long millsDiff = endZero.getTime() - startZero.getTime();
        if (millsDiff == 0) {
            return 1;
        } else {
            long days = millsDiff / (24 * 3600000);
            if (endTime.getTime() == endZero.getTime()) {
                return (int) days;
            } else {
                return (int) days + 1;
            }
        }
    }


    /**
     * 判断某个时间是否是今天
     *
     * @param date
     * @return
     */
    public static boolean dateIsToday(Date date) {
        String dateStr = StringUtils.substringBefore(dateToString(date, FORMAT_ONE), " ");
        String nowStr = StringUtils.substringBefore(dateToString(new Date(), FORMAT_ONE), " ");
        return dateStr.equals(nowStr);
    }


    public static void main(String[] args) {
        Date d = getFirstDayOfNextMonth();
        System.out.println(d.getTime());
    }

}
