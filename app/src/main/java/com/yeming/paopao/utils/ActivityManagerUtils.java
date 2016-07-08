package com.yeming.paopao.utils;

import android.app.Activity;

import java.util.ArrayList;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-02-07 17:16
 * version: V1.0
 * Description:  Activity收集以及释放
 */
public class ActivityManagerUtils {

    private static ActivityManagerUtils activityManagerUtils;
    private ArrayList<Activity> activityList = new ArrayList<Activity>();

    private ActivityManagerUtils() {

    }

    public static ActivityManagerUtils getInstance() {
        if (null == activityManagerUtils) {
            activityManagerUtils = new ActivityManagerUtils();
        }
        return activityManagerUtils;
    }

    public Activity getTopActivity() {
        return activityList.get(activityList.size() - 1);
    }

    public void addActivity(Activity ac) {
        activityList.add(ac);
    }

    /**
     * 结束所有activity
     */
    public void removeAllActivity() {
        for (Activity ac : activityList) {
            if (null != ac) {
                if (!ac.isFinishing()) {
                    ac.finish();
                }
                ac = null;
            }
        }
        activityList.clear();
    }
}
