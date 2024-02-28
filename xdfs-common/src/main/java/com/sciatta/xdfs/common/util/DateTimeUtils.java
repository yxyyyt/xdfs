package com.sciatta.xdfs.common.util;


import com.sciatta.xdfs.common.concurrent.executor.NamedThreadFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Rain on 2023/8/19<br>
 * All Rights Reserved(C) 2017 - 2023 SCIATTA <br> <p/>
 * 日期时间工具类
 */
public class DateTimeUtils {

    private static final ThreadLocal<Map<String, DateFormat>> dateFormatThreadLocal = new ThreadLocal<>();

    private static volatile long currentTimeMillis;

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    static {
        // 解决多线程下调用System.currentTimeMillis()的性能问题
        currentTimeMillis = System.currentTimeMillis();
        NamedThreadFactory namedThreadFactory = new NamedThreadFactory("current-time-millis-thread-");
        namedThreadFactory.newThread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    currentTimeMillis = System.currentTimeMillis(); // 单线程定时获取
                    try {
                        TimeUnit.MILLISECONDS.sleep(1);
                    } catch (Throwable ignore) {
                    }
                }
            }
        }).start();
    }

    private DateTimeUtils() {

    }

    /**
     * 获取当前时间戳；避免多线程下调用System.currentTimeMillis()的性能问题
     *
     * @return 当前时间戳
     */
    public static long currentTimeMillis() {
        return currentTimeMillis;
    }

    /**
     * 获取当前时间，格式是 yyyy-MM-dd HH:mm:ss
     *
     * @return 当前时间
     */
    public static String now() {
        return formatDateTime(new Date(currentTimeMillis()));
    }

    /**
     * 格式化指定日期，格式是 yyyy-MM-dd
     *
     * @param date 日期
     * @return 格式化后的日期
     */
    public static String formatDate(Date date) {
        return format(date, DATE_FORMAT);
    }

    /**
     * 格式化指定日期时间，格式是 yyyy-MM-dd HH:mm:ss
     *
     * @param date 日期
     * @return 格式化后的日期时间
     */
    public static String formatDateTime(Date date) {
        return format(date, DATE_TIME_FORMAT);
    }

    /**
     * 按照指定日期格式格式化指定日期
     *
     * @param date   日期
     * @param patten 日期格式
     * @return 格式化后的日期
     */
    public static String format(Date date, String patten) {
        return getDateFormat(patten).format(date);
    }

    /**
     * 解析日期字符串，格式是 yyyy-MM-dd
     *
     * @param dateString 日期字符串
     * @return 日期对象
     */
    public static Date parseDate(String dateString) {
        return parse(dateString, DATE_FORMAT);
    }

    /**
     * 解析日期时间字符串，格式是 yyyy-MM-dd HH:mm:ss
     *
     * @param dateString 日期时间字符串
     * @return 日期对象
     */
    public static Date parseDateTime(String dateString) {
        return parse(dateString, DATE_TIME_FORMAT);
    }

    /**
     * 按照指定日期格式解析指定日期
     *
     * @param dateString 日期字符串
     * @param pattern    日期格式
     * @return 日期对象
     */
    public static Date parse(String dateString, String pattern) {
        try {
            return getDateFormat(pattern).parse(dateString);
        } catch (Exception e) {
            //	ignore
            return null;
        }
    }

    /**
     * 指定日期增加指定天数
     *
     * @param date   指定日期
     * @param amount 指定天数
     * @return 增加天数后的日期
     */
    public static Date addDays(final Date date, final int amount) {
        return add(date, Calendar.DAY_OF_MONTH, amount);
    }

    /**
     * 指定日期增加指定月数
     *
     * @param date   指定日期
     * @param amount 指定月数
     * @return 增加月数后的日期
     */
    public static Date addMonths(final Date date, final int amount) {
        return add(date, Calendar.MONTH, amount);
    }

    /**
     * 指定日期增加指定年数
     *
     * @param date   指定日期
     * @param amount 指定年数
     * @return 增加年数后的日期
     */
    public static Date addYears(final Date date, final int amount) {
        return add(date, Calendar.YEAR, amount);
    }

    /**
     * 将毫秒数转换为秒
     *
     * @param ms 毫秒
     * @return 秒
     */
    public static long msToS(long ms) {
        return ms / 1000;
    }

    // private

    /**
     * 将日期格式字符串转换为DateFormat对象；解决SimpleDateFormat创建笨重和非线程安全问题
     *
     * @param pattern 日期格式字符串
     * @return DateFormat对象
     */
    private static DateFormat getDateFormat(String pattern) {
        if (pattern == null ||
                pattern.trim().length() == 0) {
            throw new IllegalArgumentException("pattern cannot be empty.");
        }

        Map<String, DateFormat> dateFormatMap = dateFormatThreadLocal.get();
        if (dateFormatMap != null &&
                dateFormatMap.containsKey(pattern)) {
            return dateFormatMap.get(pattern);
        }

        synchronized (dateFormatThreadLocal) {
            if (dateFormatMap == null) {
                dateFormatMap = new HashMap<>();
            }
            // SimpleDateFormat创建笨重（静态缓存）且非线程安全（同步，本地线程）
            dateFormatMap.put(pattern, new SimpleDateFormat(pattern));
            dateFormatThreadLocal.set(dateFormatMap);
        }

        return dateFormatMap.get(pattern);
    }

    /**
     * 指定日期增加年、月、日
     *
     * @param date          指定日期
     * @param calendarField 年、月、日 单位
     * @param amount        增加数量
     * @return 增加年、月、日后的日期
     */
    private static Date add(final Date date, final int calendarField, final int amount) {
        if (date == null) {
            return null;
        }
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

}
