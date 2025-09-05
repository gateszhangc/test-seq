package com.dap.sequence.api.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * 日期转换类
 * @author Sunshine
 *
 */
public class DateUtils {

    public static final String UTC_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ";
    public static final String DATE_FORMAT = "yyyyMMdd";
    public static final String ISO_DATE_FORMAT = "yyyy-MM-dd";
    public static final String ISO_DATETIME_NO_T_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 时间格式转 UTC
     * @param date  yyyy-MM-dd HH:mm:ss
     * @return yyyy-MM-dd'T'HH:mm:ss.SSSZZ
     * @throws ParseException
     */
    public static String dateFormatToUTC(String date) {
        Date d = converToDate(date, ISO_DATETIME_NO_T_FORMAT);
        return convertToString(d, UTC_DATE_FORMAT);
    }

    /**
     * 将字符串按照指定格式转换为日期
     * @param source 字符串
     * @param pattern 指定格式
     * @return 日期
     * @throws ParseException
     */
    public static Date converToDate(final String source, final String pattern) {
        try {
            return new SimpleDateFormat(pattern).parse(source);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
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

    /**
     * 取得今天对应的日期字符串
     */
    public static final String getCurrentDate() {
        return convertToString(new Date(), DATE_FORMAT);
    }

    public static final String getPrveDate(String day, Integer num) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(converToDate(day, DATE_FORMAT));
        calendar.add(Calendar.DATE, num == null ? -1 : num);
        return convertToString(calendar.getTime(), DATE_FORMAT);
    }

    public static final String getPrveDate(Date day, Integer num) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(day);
        calendar.add(Calendar.DATE, num == null ? -1 : num);
        return convertToString(calendar.getTime(), DATE_FORMAT);
    }

    public static final String getNextDate(String day) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(converToDate(day, DATE_FORMAT));
        calendar.add(Calendar.DATE, 1);
        return convertToString(calendar.getTime(), DATE_FORMAT);
    }

    public static final String getNextDate() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 1);
        return convertToString(calendar.getTime(), DATE_FORMAT);
    }

    /**
     * 取得今天当前现在对应的日期时间字符串  yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getCurrentDateTime() {
        return convertToString(new Date(), ISO_DATETIME_NO_T_FORMAT);
    }


    /**
     * 将日期转换为当日开始的日期
     */
    public static String getBeginDateOfDay(final Date date) {
        try {
            return convertToString(new Date(), ISO_DATE_FORMAT) + " 00:00:00";
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     *
     * 日期追加天数
     * @param date
     * @param addNum  X天后  -X天之前
     * @return
     */
    public static Date dateAddDay(Date date, int addNum) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, addNum);
        return cal.getTime();
    }

    /**
     *
     * 年份增加
     * @param date
     * @param addNum  X天后  -X天之前
     * @return
     */
    public static Date dateAddYear(Date date, int addNum) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, addNum);
        return cal.getTime();
    }

    /**
     *
     * 获取X小时后的时间 
     * @param
     * @return
     */
    public static String getMonthNum(int month) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR_OF_DAY, month);

        return convertToString(cal.getTime(), ISO_DATETIME_NO_T_FORMAT);
    }

    /**
     *
     * 获取X分后的时间  -X分钟之前
     * @param
     * @return
     */
    public static Date getMinuteNum(int minute) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, minute);

        return cal.getTime();
    }
}
