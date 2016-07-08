package com.yeming.paopao.utils;

import android.util.Log;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-21 20:35
 * version: V1.0
 * Description: Log工具类，设置开关，防止发布版本时log信息泄露
 */
public class LogUtil {

    private final static boolean DEBUG = true;

    public static void v(String tag, String msg) {
        if (DEBUG) {
            Log.v(tag, msg);
        }

    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, msg);
        }

    }

    public static void i(String tag, String msg) {
        if (DEBUG) {
            Log.i(tag, msg);
        }

    }

    public static void w(String tag, String msg) {
        if (DEBUG) {
            Log.w(tag, msg);
        }

    }

    public static void e(String tag, String msg) {
        if (DEBUG) {
            Log.e(tag, msg);
        }
    }
}
