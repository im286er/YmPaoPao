package com.yeming.paopao.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-24 10:30
 * version: V1.0
 * Description:  网络连接
 */
public class NetworkUtil {

    /**
     * @param context
     * @return boolean
     * 是否是Wifi链接
     */
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activityNetwork = mConnectivityManager.getActiveNetworkInfo();
            return activityNetwork != null && activityNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        }
        return false;
    }
}
