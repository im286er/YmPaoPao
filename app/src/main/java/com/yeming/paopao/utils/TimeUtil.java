package com.yeming.paopao.utils;

import java.util.Calendar;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-02-09 21:45
 * version: V1.0
 * Description:  时间格式转换
 */
public class TimeUtil {

    /**
     * @param time
     * @return
     * 时间间隔
     */
    public static String dayToNow(long time) {
        Calendar now = Calendar.getInstance();
        long minute = (now.getTimeInMillis() - time) / 60000;
        if (minute < 60) {
            if (minute == 0) {
                return "刚刚";
            } else {
                return minute + "分钟前";
            }
        }
        long hour = minute / 60;
        if (hour < 24) {
            return hour + "小时前";
        }
        long day = hour / 24;
        if (day < 30) {
            return day + "天前";
        }
        long month = day / 30;
        if (month < 11) {
            return month + "个月前";
        }
        long year = month / 12;
        return year + "年前";
    }
}
