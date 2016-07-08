package com.yeming.paopao.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.MemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.LimitedAgeMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.yeming.paopao.bean.User;
import com.yeming.paopao.commons.Constant;
import com.yeming.paopao.utils.ActivityManagerUtils;
import com.yeming.paopao.utils.LogUtil;
import com.yeming.paopao.utils.SharedPreHelperUtil;

import java.io.File;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-19 22:45
 * version: V1.0
 * Description:
 */
public class YmApplication extends Application {

    public final String TAG = "YmApplication" ;
    public static YmApplication mInstance;
    public static User user;
    public static float sScale;
    public static int sWidthDp;
    public static int sWidthPix;
    public static BmobGeoPoint lastPoint = null; // 上一次定位到的经纬度
    public static String locationAddrStr = "" ;   //获取反地理编码
    public static String locationCity = "" ;    //城市
    public static String locationDistrict = "" ; // 区县
    //字体样式
    public static Typeface chineseTypeface,englishTypeface ;
    public LocationClient mLocationClient;
    public MyLocationListener mMyLocationListener;

    private String latitude = "";
    private String longtitude = "";



    public static synchronized YmApplication getInstance() {
        if (mInstance == null) {
            mInstance = new YmApplication();
        }
        return mInstance;
    }

    /**
     * 获取当前登录用户
     * @return  User
     */
    public static synchronized  User getCurrentUser(){
        return BmobUser.getCurrentUser(mInstance,User.class) ;
    }

    /**
     * 初始化ImageLoader
     */
    public static void initImageLoader(Context context) {
        File cacheDir = StorageUtils.getOwnCacheDirectory(context,
                Constant.OwnCacheDirectory);// 获取到缓存的目录地址

        /*DiskCache discCache = new LimitedAgeDiskCache(cacheDir,15 * 60) ;
        DiskCache diskCache1 = new UnlimitedDiskCache(cacheDir) ;
       LruMemoryCache lruMemoryCache = new LruMemoryCache(5 * 1024 * 1024) ;
       MemoryCache memoryCache = new LimitedAgeMemoryCache(lruMemoryCache,15 * 60) ;*/

        // 创建配置ImageLoader
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                // 线程池内加载的数量
                .threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCache(new WeakMemoryCache())
               // .memoryCache(memoryCache)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                        // 将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)

                .discCache(new UnlimitedDiskCache(cacheDir))// 自定义缓存路径
                 //       .diskCache(discCache)
                        // .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .writeDebugLogs() // Remove for release app
                .discCacheSize(50 * 1024 * 1024)  // 50 Mb
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);// 全局初始化此配置
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sScale = getResources().getDisplayMetrics().density;
        sWidthPix = getResources().getDisplayMetrics().widthPixels;
        sWidthDp = (int) (sWidthPix / sScale);

        mInstance = this ;

        init();
        initTypeFace(getApplicationContext());
    }

    /**
     * 初始化百度定位sdk
     */
    private void initBaiduLocClient() {
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
     //   option.setCoorType("bd09ll"); // 设置坐标类型
        option.setAddrType("all");// 返回的定位结果包含地址信息
     //   option.disableCache(false);// 禁止启用缓存定位
        option.setNeedDeviceDirect(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
    //    option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
        mLocationClient.setLocOption(option);
        mLocationClient.start();
        mLocationClient.requestLocation() ;
    }


    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // Receive Location
            double latitude = location.getLatitude();
            double longtitude = location.getLongitude();
            LogUtil.i(TAG,"---------location District="+locationDistrict);
            //    locationAddrStr = location.getAddrStr();
            //    locationCity = location.getCity() ;

            //    LogUtil.i(TAG,"---------location address="+locationAddrStr);
            //    LogUtil.i(TAG,"---------location City="+locationCity);
            locationDistrict = location.getDistrict() ;
            if (lastPoint != null && locationDistrict != null) {
                if (lastPoint.getLatitude() == location.getLatitude()
                       && lastPoint.getLongitude() == location.getLongitude()) {
                    // 若两次请求获取到的地理位置坐标是相同的，则不再定位
                    LogUtil.i(TAG,"两次获取坐标相同");
                    Intent intent = new Intent() ;
                    intent.setAction(Constant.USER_LOCATION_CHANGE) ;
                    intent.putExtra("location",locationDistrict) ;
                    sendBroadcast(intent);
                    mLocationClient.stop();
                    return;
                }
            }

            lastPoint = new BmobGeoPoint(longtitude, latitude);
        }
    }

    /**
     * @param context
     * 初始化字体风格
     */
    private void initTypeFace(Context context) {
        // TODO Auto-generated method stub
        englishTypeface = Typeface.createFromAsset(context.getAssets(),
                "font/Roboto-Light.ttf");
        chineseTypeface = Typeface.createFromAsset(context.getAssets(),
                "font/xiyuan.ttf");
    }

    /**
     *
     */
    public void init() {
        initImageLoader(getApplicationContext());
        SDKInitializer.initialize(this);
        initBaiduLocClient() ;
    }

    /**
     * 退出登录,清空缓存数据
     */
    public void logout() {
        BmobUser.logOut(this);
        User.logOut(this);
    //    setLatitude(null);
    //    setLongtitude(null);
        SharedPreHelperUtil.getInstance(this).clear();
        clearCache(null);
        exit();
    }

    /**
     * 删除缓存文件
     * @param cacheFile
     */
    public void clearCache(File cacheFile) {
        if (cacheFile == null) {
            try {
                File cacheDir = this.getCacheDir() ;
                File filesDir = this.getFilesDir();
                if (cacheDir.exists()) {
                    clearCache(cacheDir);
                    clearCache(filesDir);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (cacheFile.isFile()) {
            cacheFile.delete();
        } else if (cacheFile.isDirectory()) {
            File[] childFiles = cacheFile.listFiles();
            for (int i = 0; i < childFiles.length; i++) {
                clearCache(childFiles[i]);
            }
        }
    }

    /**
     * 设置维度
     */
    public void setLatitude(String lat) {
        SharedPreHelperUtil.getInstance(this).setLatitude(lat);
    }
    /**
     * 设置经度
     */
    public void setLongtitude(String lon){
        SharedPreHelperUtil.getInstance(this).setLongtitude(lon);
    }

    /**
     * 获取维度
     * @return
     */
    public String getLatitude(){
        latitude = SharedPreHelperUtil.getInstance(this).getLatitude() ;
        return latitude ;
    }
    /**
     * 获取经度
     * @return
     */
    public String getLongtitude(){
        longtitude = SharedPreHelperUtil.getInstance(this).getLongtitude() ;
        return longtitude ;
    }

    public void addActivity(Activity ac){
        ActivityManagerUtils.getInstance().addActivity(ac);
    }

    public void exit(){
        ActivityManagerUtils.getInstance().removeAllActivity();
    }

    public Activity getTopActivity(){
        return ActivityManagerUtils.getInstance().getTopActivity();
    }
}
