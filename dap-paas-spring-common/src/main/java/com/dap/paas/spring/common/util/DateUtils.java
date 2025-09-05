package com.dap.paas.spring.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期格式转换
 *
 */
public class DateUtils {

    public static final String DATA_FORMAT_YEAR_MON_DAY_HOUR_MIN_SEC = "yyyy-MM-dd HH:mm:ss";
    public static final String DATA_FORMAT_YEAR = "yyyyMMdd";
    public static final String DATA_FORMAT_SPLIT_YEAR = "yyyy-MM-dd";

    private DateUtils() {

    }

    /**
     * 转换时间为unix时间,默认格式yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return 1498443597
     * @throws ParseException
     */
    public static long convertDate2UnixTime(String date) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(DATA_FORMAT_YEAR_MON_DAY_HOUR_MIN_SEC);
        return df.parse(date).getTime();
    }

    /**
     *
     * @param timeMill
     * @return
     */
    public static String convertTimeMill2Date(long timeMill) {
        long day = timeMill / (3600 * 24);
        long hour = (timeMill - 3600 * 24 * day) / (60 * 60);
        long min = (timeMill - 3600 * 24 * day - 3600 * hour) / 60;
        long sec = timeMill - 3600 * 24 * day - 3600 * hour - 60 * min;
        return day + "天" + hour + "小时" + min + "分" + sec + "秒";
    }

    /**
     * 转换unix时间为时间,默认格式yyyy-MM-dd HH:mm:ss.
     *
     * @param unixTime
     * @return Date String.
     */
    public static String convertUnixTime(long unixTime) {
        return convertUnixTime(unixTime, DATA_FORMAT_YEAR_MON_DAY_HOUR_MIN_SEC);
    }

    /**
     * 转换unix时间为时间
     *
     * @param unixTime
     * @param formatter
     * @return
     */
    public static String convertUnixTime(long unixTime, String formatter) {
        SimpleDateFormat df = new SimpleDateFormat(formatter);
        return df.format(new Date(unixTime));
    }

    /**
     * 转换unix时间为时间
     * @param unixTime unix时间戳
     * @return 1907-01-01 00:00:00
     */
    public static String convertUnixTime2DateStr(long unixTime) {
        SimpleDateFormat df = new SimpleDateFormat(DATA_FORMAT_YEAR_MON_DAY_HOUR_MIN_SEC);
        return df.format(new Date(unixTime));
    }

    /**
     * 转换unix时间为时间
     * @param unixTime unix时间戳
     * @return 1907-01-01 00:00:00
     */
    public static Date convertUnixTime2Date(long unixTime) {
        return new Date(unixTime);
    }

    /** 获取当前秒 */
    public static String getCurrentDateStr() {
        SimpleDateFormat df = new SimpleDateFormat(DATA_FORMAT_YEAR_MON_DAY_HOUR_MIN_SEC);
        return df.format(new Date());
    }

    /** 获取当前日期 */
    public static Date getCurrentDate() {
        return new Date();
    }

    /**
     * 取得今天对应的日期字符串
     */
    public static final String getStrCurrentDate() {
        return convertToString(new Date(), DATA_FORMAT_YEAR);
    }

    /**
     * 将日期按照指定模式转换为字符串
     * @param date 日期
     * @param pattern 指定模式
     * @return 转换后的字符串
     */
    public static String convertToString(final Date date, final String pattern) {
        if (date != null) {
            return new SimpleDateFormat(pattern).format(date);
        } else {
            return null;
        }
    }

    /** 获取当前unix时间 */
    public static long getTimeSpan() {
        return System.currentTimeMillis();
    }

    /** 获取当前时间，指定格式 */
    public static String getCustomDate(String formatter) {
        SimpleDateFormat df = new SimpleDateFormat(formatter);
        return df.format(new Date());
    }

    /** 获取当前日 */
    public static String getCustomLastDay(int day) {
        SimpleDateFormat df = new SimpleDateFormat(DATA_FORMAT_YEAR);
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -day);
        return df.format(calendar.getTime());
    }

    /** 获取当前日期 */
    public static String getCustomLastDay(String formatter, int day) {
        SimpleDateFormat df = new SimpleDateFormat(formatter);
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -day);
        return df.format(calendar.getTime());
    }

    /** 转换日期格式 */
    public static String convertDate2Date(String date) throws ParseException {
        SimpleDateFormat newly = new SimpleDateFormat(DATA_FORMAT_SPLIT_YEAR);
        SimpleDateFormat oldly = new SimpleDateFormat(DATA_FORMAT_YEAR);
        return newly.format(oldly.parse(date));
    }

    /** 计算日期差 */
    public static int getDiffDay(String beforeDate, String afterDate) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(DATA_FORMAT_YEAR);
        return Integer.parseInt((df.parse(afterDate).getTime() - df.parse(beforeDate).getTime()) / (1000 * 3600 * 24) + "");
    }

}
