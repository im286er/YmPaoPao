package com.yeming.paopao.aty;


import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yeming.paopao.R;
import com.yeming.paopao.app.YmApplication;
import com.yeming.paopao.bean.User;
import com.yeming.paopao.commons.Constant;
import com.yeming.paopao.commons.DataInfoCache;
import com.yeming.paopao.commons.LoadStartBackground;
import com.yeming.paopao.commons.NavigateType;
import com.yeming.paopao.fmt.DrawerFragment;
import com.yeming.paopao.fmt.MainFragment;
import com.yeming.paopao.proxy.UserProxy;
import com.yeming.paopao.utils.LogUtil;
import com.yeming.paopao.utils.SharedPreHelperUtil;
import com.yeming.paopao.views.ExitActionView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-19 22:46
 * version: V1.0
 * Description:
 */
public class MainActivity extends FragmentActivity implements ExitActionView.OnActionSheetSelected,
        DialogInterface.OnCancelListener,UserProxy.IGetFocusListener,UserProxy.IGetPaopaoListener,
        UserProxy.IGetFansListener,UserProxy.IGetFansListListener,UserProxy.IGetFocusListListener{

    private static final String TAG = "MainActivity" ;
    private Context context ;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private FrameLayout mFrameLayout;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;
    private FragmentManager fragmentManager;
    private MainFragment mainFragment ;

    private UserProxy userProxy ;
    private boolean drawerStatus  ;   //  true :open   false:close

    public static float density;
    public static float xdpi;
    public static float ydpi;
    public static float screenWidth;
    public static float screenHeight;
    public static float densityDPI;
    public static int screenWidthDip;
    public static int screenHeightDip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.main_layout);

        //Bmob.initialize(getApplicationContext(), Constant.APP_ID);

        YmApplication.getInstance().addActivity(this);
        context = this ;

        fragmentManager = getSupportFragmentManager() ;
        userProxy = new UserProxy(context) ;
        setActionBarStyle();

        getDisplayDp() ;

        // 默认选中MainFragment
        setNavigateSelected(NavigateType.HOME.getValue());

        mTitle = mDrawerTitle = getTitle();
        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.content_drawer);
        mFrameLayout = (FrameLayout) findViewById(R.id.left_drawer);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.left_drawer,
                        new DrawerFragment(MainActivity.this)).commit();
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.icon_drawer, R.string.drawer_open,
                R.string.drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                drawerStatus = false ;
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                drawerStatus = true ;
            }
        } ;
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
        setmDrawerLayout(mDrawerLayout);
        drawerStatus = false ;  //  默认关闭

        User user = BmobUser.getCurrentUser(context,User.class) ;

        userProxy.setOnGetFansListener(this);
        userProxy.setOnGetFocusListener(this);
        userProxy.setOnGetPaopaoListener(this);
        userProxy.setOnGetFansListListener(this);
        userProxy.setOnGetFocusListListener(this);

        userProxy.getUserFansCountNum(user);                  //  更新粉丝数
        userProxy.getUserFocusCountNum(user);                 //  更新关注数
        userProxy.getUserPaoPaoNum(user.getObjectId());  //  更新泡泡数

        userProxy.getUserFansList(user);
        userProxy.getUserFocusList(user);

        updateStarBg();                                    //  更新启动背景图
        updateUserLocation();

    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setActionBarStyle() {
        this.getActionBar().setTitle("YmPaoPao");
        getActionBar().setBackgroundDrawable(
                this.getBaseContext().getResources()
                        .getDrawable(R.drawable.actionbar_bg)); //R.drawable.actionbar_back
       // getActionBar().setIcon(R.drawable.ic_launcher);
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        int titleId = Resources.getSystem().getIdentifier("action_bar_title",
                "id", "android");
        TextView textView = (TextView) findViewById(titleId);
        Typeface typeface = Typeface.createFromAsset(getAssets(),
                "font/Wendy.ttf");
        textView.setTypeface(typeface);
        textView.setTextColor(0xFFdfdfdf);
        textView.setTextSize(32);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    //   getActionBar().setDisplayUseLogoEnabled(false);
    //   getActionBar().setDisplayShowHomeEnabled(false);
        //setHomeButtonEnabled(true);
       getActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    public DrawerLayout getmDrawerLayout() {
        return mDrawerLayout;
    }

    public void setmDrawerLayout(DrawerLayout mDrawerLayout) {
        this.mDrawerLayout = mDrawerLayout;
    }


    /**
     * @param navigateType
     *
     * 显示切换选中的导航项
     */
    public void setNavigateSelected(int navigateType){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (navigateType){
            case 0:
                hideFragments(transaction);
                if (null == mainFragment) {
                    mainFragment = new MainFragment(MainActivity.this);
                    transaction.add(R.id.main_content, mainFragment);
                } else {
                    transaction.show(mainFragment);
                }
                break ;
        }
        transaction.commit();
    }

    /**
     * @param transaction
     *
     * 将所有fragment都置为隐藏状态
     */
    private void hideFragments(FragmentTransaction transaction) {
        if(mainFragment != null){
            transaction.hide(mainFragment) ;
        }
    }

    @Override
    public void onBackPressed() {
       // BmobUser.logOut(this);   //清除缓存用户对象
       // super.onBackPressed();
        if(drawerStatus){
            mDrawerLayout.closeDrawers();
            return ;
        }
        ExitActionView.showExitActionSheet(this,this,this) ;
    }

    @Override
    public void onClick(int whichButton) {
        switch (whichButton){
            case 0:   //  点击了退出
                YmApplication.getInstance().exit();
                break ;
            case 1:   // 点击了取消
                break ;
            default:
                break;
        }
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {

    }

    private void getDisplayDp() {
        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        density = dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
        densityDPI = dm.densityDpi; // 屏幕密度（每寸像素：120/160/240/320）
        xdpi = dm.xdpi;
        ydpi = dm.ydpi;
        screenWidthDip = dm.widthPixels; // 屏幕宽（dip，如：320dip）
        screenHeightDip = dm.heightPixels; // 屏幕宽（dip，如：533dip）
        screenWidth = (int) (dm.widthPixels * density + 0.5f); // 屏幕宽（px，如：480px）
        screenHeight = (int) (dm.heightPixels * density + 0.5f); // 屏幕高（px，如：800px）
    };


    /**
     * 进入主页更新启动页背景
     */
    public void updateStarBg(){
        LoadStartBackground loadStartBackground = new LoadStartBackground(getApplicationContext()) ;
        loadStartBackground.updateStartBackground();
    }

    /**
     * 更新用户的经纬度信息
     */
    private void updateUserLocation(){
        if(YmApplication.lastPoint != null){
            String saveLatitude  = YmApplication.getInstance().getLatitude();
            String saveLongtitude = YmApplication.getInstance().getLongtitude();
            String newLat = String.valueOf(YmApplication.lastPoint.getLatitude());
            String newLong = String.valueOf(YmApplication.lastPoint.getLongitude());
            if(!saveLatitude.equals(newLat)|| !saveLongtitude.equals(newLong)){//只有位置有变化就更新当前位置，达到实时更新的目的
                User u = BmobUser.getCurrentUser(this, User.class);
                final User user = new User();
                user.setLocationPoint(YmApplication.lastPoint);
                user.setObjectId(u.getObjectId());
                user.update(this,new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        YmApplication.getInstance().setLatitude(String.valueOf(user.getLocationPoint().getLatitude()));
                        YmApplication.getInstance().setLongtitude(String.valueOf(user.getLocationPoint().getLongitude()));
                    }
                    @Override
                    public void onFailure(int code, String msg) {
                        // TODO Auto-generated method stub
                    }
                });
            }else{
                //ShowLog("用户位置未发生过变化");
            }
        }
    }

    @Override
    public void onGetFansSuccess(int count) {
        LogUtil.d(TAG, "-------onGetFansSuccess --" + count);
        // 保存粉丝数
         SharedPreHelperUtil.getInstance(context).setUserFansNum(count);
      // 发送更新粉丝数的广播
      /*   Intent intent = new Intent() ;
         intent.setAction(Constant.USER_FANSNUM_CHANGE) ;
         context.sendBroadcast(intent);*/
    }

    @Override
    public void onGetFansFailure(int code, String msg) {
        LogUtil.d(TAG, "------onGetFansFailure-----code==" + code + "---" + msg);
    }

    @Override
    public void onGetFocusSuccess(int count) {
        LogUtil.d(TAG, "-------onGetFocusSuccess --" + count);
        // 保存关注数
        SharedPreHelperUtil.getInstance(context).setUserFocusNum(count);
        // 发送更新关注数的广播
        /*Intent intent = new Intent() ;
        intent.setAction(Constant.USER_FOCUSNUM_CHANGE) ;
        context.sendBroadcast(intent);*/
    }

    @Override
    public void onGetFocusFailure(int code, String msg) {
        LogUtil.d(TAG, "------onGetFocusFailure-----code==" + code + "---" + msg);
    }

    @Override
    public void onGetPaopaoSuccess(int count) {
        LogUtil.d(TAG, "-------onGetPaopaoSuccess --" + count);
        SharedPreHelperUtil.getInstance(context).setUserPaopaoNum(count);
        // 发送更新泡泡数的广播
        Intent intent = new Intent() ;
        intent.setAction(Constant.USER_PAOPAONUM_CHANGE) ;
        context.sendBroadcast(intent);
    }

    @Override
    public void onGetPaopaoFailure(int code, String msg) {
        LogUtil.d(TAG, "------onGetPaopaoFailure-----code==" + code + "---" + msg);
    }

    @Override
    public void onGetFansListSuccess(List<User> list) {
        LogUtil.d(TAG, "-------onGetFansListSuccess --" + list.size());
        DataInfoCache.saveUserFansList(context,(ArrayList)list);
    }

    @Override
    public void onGetFansListFailure(int code, String msg) {
        LogUtil.d(TAG, "------onGetFansListFailure-----code==" + code + "---" + msg);
    }

    @Override
    public void onGetFocusListSuccess(List<User> list) {
        LogUtil.d(TAG, "-------onGetFocusListSuccess --" + list.size());
        DataInfoCache.saveUserFocusList(context,(ArrayList)list);
    }

    @Override
    public void onGetFocusListFailure(int code, String msg) {
        LogUtil.d(TAG, "------onGetFocusListFailure-----code==" + code + "---" + msg);
    }
}
