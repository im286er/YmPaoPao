package com.yeming.paopao.aty;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.yeming.paopao.R;
import com.yeming.paopao.app.YmApplication;
import com.yeming.paopao.bean.User;
import com.yeming.paopao.commons.DataInfoCache;
import com.yeming.paopao.proxy.UserProxy;
import com.yeming.paopao.utils.LogUtil;
import com.yeming.paopao.utils.SharedPreHelperUtil;

import java.util.ArrayList;
import java.util.List;

import static com.yeming.paopao.proxy.UserProxy.IUserFocusOtherListener;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-28 21:27
 * version: V1.0
 * Description:   其他用户信息页面  不可修改
 */
public class OtherUserInfoActivity extends Activity implements View.OnClickListener,
        UserProxy.IGetFansListener,UserProxy.IGetPaopaoListener,UserProxy.IGetFocusListener,
        IUserFocusOtherListener,UserProxy.IGetFocusListListener{

    private static final String TAG = "OtherUserInfoActivity";
    private Context context ;
    private RelativeLayout userIconLayout ,userNickLayout ,userSignLayout,userPaopaoLayout,userFansLayout,userFocusLayout;
    private TextView userIconTips,userNickTips,userNickText,userSexTips,userSignTips,userSignText,userFansTips,userFansText,userFocusTips,userFocusText,userPaopaoTips,userPaopaoText ;
    private Button focusBut,messageBut ;
    private ImageView userIconImage ;
    private CheckBox sexSwitch;
    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.user_icon_default_main)
            .showImageOnFail(R.drawable.user_icon_default_main)
            .resetViewBeforeLoading(true)
            .cacheOnDisc(true)
            .imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .considerExifParams(true)
            .displayer(new FadeInBitmapDisplayer(300))
            .build();
    private User user ;
    private UserProxy userProxy ;
    private ArrayList<User> focusList ;
    private boolean focused = false ;  //  是否关注
    private boolean isGetCurrentUserFocus = false ;  //  是否是获取当前登录用户的关注数量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.other_userinfo_layout);

        YmApplication.getInstance().addActivity(this);
        context = this ;

        Intent intent = getIntent() ;
        user = (User) intent.getSerializableExtra("user") ;

        setActionBar() ;

        userProxy = new UserProxy(context) ;
        focusList = new ArrayList<User>() ;
        focusList = DataInfoCache.loadUserFocusList(context) ;

        initView();
        initListener();

        setUserData();
    }



    /**
     * 初始化控件
     */
    private void initView() {
        /*userIconLayout = (RelativeLayout) findViewById(R.id.user_icon);
        userNickLayout = (RelativeLayout) findViewById(R.id.user_nick);
        userSignLayout = (RelativeLayout) findViewById(R.id.user_sign);*/
        userFansLayout = (RelativeLayout) findViewById(R.id.user_fans);
        userFocusLayout = (RelativeLayout) findViewById(R.id.user_focus);
        userPaopaoLayout = (RelativeLayout) findViewById(R.id.user_paopao);

        userIconTips = (TextView) findViewById(R.id.user_icon_tips);
        userNickTips = (TextView) findViewById(R.id.user_nick_tips);
        userNickText = (TextView) findViewById(R.id.user_nick_text);
        userSexTips = (TextView) findViewById(R.id.sex_choice_tips);
        userSignTips = (TextView) findViewById(R.id.user_sign_tips);
        userSignText = (TextView) findViewById(R.id.user_sign_text);
        userPaopaoTips = (TextView) findViewById(R.id.user_paopao_tips);
        userPaopaoText = (TextView) findViewById(R.id.user_paopao_text);
        userFansTips = (TextView) findViewById(R.id.user_fans_tips);
        userFocusTips = (TextView) findViewById(R.id.user_focus_tips);
        userFansText = (TextView) findViewById(R.id.user_fans_text);
        userFocusText = (TextView) findViewById(R.id.user_focus_text);

        focusBut = (Button) findViewById(R.id.user_is_focus);
        messageBut = (Button) findViewById(R.id.user_send_message);

        userIconImage = (ImageView) findViewById(R.id.user_icon_image);

        sexSwitch = (CheckBox) findViewById(R.id.sex_choice_switch);
        sexSwitch.setClickable(false);   //  性别不可点击

        userIconTips.setTypeface(YmApplication.chineseTypeface);
        userNickTips.setTypeface(YmApplication.chineseTypeface);
        userNickText.setTypeface(YmApplication.chineseTypeface);
        userSexTips.setTypeface(YmApplication.chineseTypeface);
        userSignTips.setTypeface(YmApplication.chineseTypeface);
        userSignText.setTypeface(YmApplication.chineseTypeface);

        userFansTips.setTypeface(YmApplication.chineseTypeface);
        userFocusText.setTypeface(YmApplication.chineseTypeface);
        userFansText.setTypeface(YmApplication.chineseTypeface);
        userFocusTips.setTypeface(YmApplication.chineseTypeface);
        userPaopaoTips.setTypeface(YmApplication.chineseTypeface);
        userPaopaoText.setTypeface(YmApplication.chineseTypeface);

        focusBut.setTypeface(YmApplication.chineseTypeface);
        messageBut.setTypeface(YmApplication.chineseTypeface);

    }

    /**
     * 设置用户数据
     */
    private void setUserData(){

        String sign = user.getSign() ;
        String nick = user.getNickname() ;
        String avaterUrl = user.getAvatarUrl() ;
        String sex = user.getSex() ;

        userNickText.setText(nick);
        userSignText.setText(sign);
        if("m".equals(sex)){
            sexSwitch.setChecked(true);
        }else if("w".equals(sex)){
            sexSwitch.setChecked(false);
        }
        ImageLoader.getInstance().displayImage(avaterUrl,userIconImage,options);

        userProxy.getUserFansCountNum(user);
        userProxy.getUserFocusCountNum(user);
        userProxy.getUserPaoPaoNum(user.getObjectId());

        //  设置关注状态
        if(focusList != null && focusList.size() > 0){
            if(userIsFocus(focusList)){
                focusBut.setText(R.string.cancle_focus);
                focused = true ;
            }
        }
    }

    /**
     * 当前用户是否关注此用户
     * @return
     */
    private boolean userIsFocus(ArrayList<User> list){
        boolean isFocus = false ;
        for (int i = 0; i < list.size(); i++) {
            if(user.getObjectId().equals(list.get(i).getObjectId())){
                isFocus = true ;
                break ;
            }
        }
        return isFocus ;
    }


    /**
     * actionbar style
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void setActionBar(){
        //this.getActionBar().setTitle("PaoPao");
        getActionBar().setBackgroundDrawable(
                this.getBaseContext().getResources()
                        .getDrawable(R.drawable.actionbar_bg));
        //getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        //getActionBar().setTitle(user.getNickname());
        int titleId = Resources.getSystem().getIdentifier("action_bar_title",
                "id", "android");
        TextView textView = (TextView) findViewById(titleId);
        textView.setTypeface(YmApplication.chineseTypeface);
        textView.setTextColor(0xFFdfdfdf);
        textView.setTextSize(20);
    }

    /**
     * 注册监听器
     */
    private void initListener(){
        userFansLayout.setOnClickListener(this);
        userFocusLayout.setOnClickListener(this);
        userPaopaoLayout.setOnClickListener(this);
        userIconImage.setOnClickListener(this);
        focusBut.setOnClickListener(this);
        messageBut.setOnClickListener(this);

        userProxy.setOnGetFocusListener(this);
        userProxy.setOnGetPaopaoListener(this);
        userProxy.setOnGetFansListener(this);
        userProxy.setOnGetFocusListListener(this);
        userProxy.setOnUserFocusOtherListener(this);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId() ;
        switch (id){
            case android.R.id.home:
                onBackPressed();
                break ;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent() ;
        switch (view.getId()){
            case R.id.user_icon_image:
                intent.setClass(context, ZoomImageActivity.class);
                intent.putExtra("url",user.getAvatarUrl()) ;
                startActivity(intent);
                break ;
            case R.id.user_fans:
                intent.setClass(context, FansListActivity.class);
                intent.putExtra("user",user) ;
                startActivity(intent);
                break ;
            case R.id.user_focus:
                intent.setClass(context, FocusListActivity.class);
                intent.putExtra("user",user) ;
                startActivity(intent);
                break ;
            case R.id.user_paopao:
                intent.setClass(context, UserPaopaoListActivity.class);
                intent.putExtra("user",user) ;
                startActivity(intent);
                break ;
            case R.id.user_is_focus:
                if(focused){    ///  已关注，点击则取消关注
                    Toast.makeText(context,"取消关注，功能未完成！",Toast.LENGTH_SHORT).show();
                    LogUtil.d(TAG,"-----focused true-----");
                }else{        //未关注，点击则关注
                    LogUtil.d(TAG,"-----focused false-----");
                    userProxy.userFocusOther(user);
                }
                break ;
            case R.id.user_send_message:
                break ;
            default:
                break ;
        }
    }

    @Override
    public void onGetFansSuccess(int count) {
        LogUtil.d(TAG, "-------onGetFansSuccess --" + count);
        userFansText.setText(count+"");
    }

    @Override
    public void onGetFansFailure(int code, String msg) {
        LogUtil.d(TAG, "------onGetFansFailure-----code==" + code + "---" + msg);
    }

    @Override
    public void onGetFocusSuccess(int count) {
        LogUtil.d(TAG, "-------onGetFocusSuccess --" + count);
        if(isGetCurrentUserFocus){
            LogUtil.d(TAG, "-------onGetFocusSuccess --isGetCurrentUserFocus=" + isGetCurrentUserFocus);
            SharedPreHelperUtil.getInstance(context).setUserFocusNum(count);
            /*// 发送更新关注数的广播
            Intent intent = new Intent() ;
            intent.setAction(Constant.USER_FOCUSNUM_CHANGE) ;
            context.sendBroadcast(intent);*/
        }else{    /// 更新当前页面数量
            LogUtil.d(TAG, "-------onGetFocusSuccess --isGetCurrentUserFocus=" + isGetCurrentUserFocus);
            userFocusText.setText(count+"");
        }

    }

    @Override
    public void onGetFocusFailure(int code, String msg) {
        LogUtil.d(TAG, "------onGetFocusFailure-----code==" + code + "---" + msg);
        isGetCurrentUserFocus = false ;
    }

    @Override
    public void onGetPaopaoSuccess(int count) {
        LogUtil.d(TAG, "-------onGetPaopaoSuccess --" + count);
        userPaopaoText.setText(count+"");
    }

    @Override
    public void onGetPaopaoFailure(int code, String msg) {
        LogUtil.d(TAG, "------onGetPaopaoFailure-----code==" + code + "---" + msg);
    }

    @Override
    public void onUserFocusOtherSuccess(String s) {
        LogUtil.d(TAG, "-------onUserFocusOtherSuccess --" + s);
        focusBut.setText(R.string.cancle_focus);
        focused = true ;
        // 更新登录用户的缓存的关注列表
        userProxy.getUserFocusList(YmApplication.getCurrentUser());
       // focusList.add(user) ;
        //本地更新登录用户的缓存的关注列表
       // DataInfoCache.saveUserFocusList(context,focusList);
        //  关注成功后，刷新此用户页面（即当前页面）粉丝数量
        userProxy.getUserFansCountNum(user);
        // 同时更新页面上登录用户的关注数量
        isGetCurrentUserFocus = true ;
        userProxy.getUserFocusCountNum(YmApplication.getCurrentUser());
    }

    @Override
    public void onUserFocusOtherFailure(int code, String msg) {
        LogUtil.d(TAG, "------onUserFocusOtherFailure-----code==" + code + "---" + msg);
        focused = false ;
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
