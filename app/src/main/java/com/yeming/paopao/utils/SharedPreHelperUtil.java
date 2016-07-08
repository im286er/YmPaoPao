package com.yeming.paopao.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.yeming.paopao.commons.Constant;

import java.util.Calendar;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-24 22:49
 * version: V1.0
 * Description:  配置参数信息
 */
public class SharedPreHelperUtil {

    private static final String TAG = "SharedPreHelperUtil";
    private static SharedPreHelperUtil sharedPreHelper = null;
    private SharedPreferences sharedPreferences;

    public SharedPreHelperUtil(Context ctx) {
        sharedPreferences = ctx.getSharedPreferences(Constant.GLOBAL_SETTING, ctx.MODE_PRIVATE);
    }

    public static SharedPreHelperUtil getInstance(Context ctx) {
        if (sharedPreHelper == null) {
            sharedPreHelper = new SharedPreHelperUtil(ctx);
        }
        return sharedPreHelper;
    }

    /**
     * @param ctx
     * @return 距离上次检查24小时后再检查
     */
    public static boolean checkStartBackgroundTime(Context ctx) {
        long last = ctx.getSharedPreferences(Constant.GLOBAL_SETTING, Context.MODE_PRIVATE)
                .getLong(Constant.START_BACKGROUND_SETTING_TIME, 0);
        LogUtil.d(TAG, "---------START_BACKGROUND_SETTING_TIME=" + last + "--------=" + Calendar.getInstance().getTimeInMillis());
        if (last == 0) {  //没有更新过返回0  则返回true 更新
            return true;
        }
        return (Calendar.getInstance().getTimeInMillis() - last) > 1000 * 3600 * 24;
    }

    /**
     * 清除用户数据
     */
    public void clear(){
        sharedPreferences.edit().clear().commit() ;
    }

    /**
     * 保存启动图片更新时间
     */
    public void setStartBackgroundUpdateTime() {
        sharedPreferences.edit().putLong(Constant.START_BACKGROUND_SETTING_TIME, Calendar.getInstance().getTimeInMillis()).commit();
        LogUtil.d(TAG, "-------setStartBackgroundUpdateTime success-------------");
    }

    /**
     * 设置经度
     */
    public void setLongtitude(String lon) {
        sharedPreferences.edit().putString(Constant.PREF_LONGTITUDE,lon).commit() ;
    }

    /**
     * 获取经度
     * @return
     */
    public String getLongtitude() {
        return sharedPreferences.getString(Constant.PREF_LONGTITUDE, "");
    }

    /**
     * 设置维度
     * @param lat
     */
    public void setLatitude(String lat) {
        sharedPreferences.edit().putString(Constant.PREF_LATITUDE,lat).commit() ;
    }

    /**
     * 获取纬度
     * @return
     */
    public String getLatitude() {
        return sharedPreferences.getString(Constant.PREF_LATITUDE, "");
    }

    /**
     * 保存粉丝数
     * @param fansNum
     */
    public void setUserFansNum(int fansNum){
        sharedPreferences.edit().putInt(Constant.FANS_NUM,fansNum).commit() ;
    }

    /**
     * 获取粉丝数
     * @return
     */
    public int getUserFansNum(){
        return sharedPreferences.getInt(Constant.FANS_NUM,0) ;
    }

    /**
     * 保存关注数
     * @param focusNum
     */
    public void setUserFocusNum(int focusNum){
        sharedPreferences.edit().putInt(Constant.FOCUS_NUM,focusNum).commit() ;
    }

    /**
     * 获取关注数
     * @return
     */
    public int getUserFocusNum(){
        return sharedPreferences.getInt(Constant.FOCUS_NUM,0) ;
    }

    /**
     * 保存用户泡泡数
     * @param paopaoNum
     */
    public void setUserPaopaoNum(int paopaoNum){
        sharedPreferences.edit().putInt(Constant.PAOPAO_NUM,paopaoNum).commit() ;
    }

    /**
     * 获取用户泡泡数
     * @return
     */
    public int getUserPaopaoNum(){
        return sharedPreferences.getInt(Constant.PAOPAO_NUM,0) ;
    }
}
