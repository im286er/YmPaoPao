package com.yeming.paopao.aty;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.yeming.paopao.R;
import com.yeming.paopao.app.YmApplication;
import com.yeming.paopao.bean.StartBackground;
import com.yeming.paopao.bean.User;
import com.yeming.paopao.commons.Constant;
import com.yeming.paopao.commons.LoadStartBackground;
import com.yeming.paopao.proxy.UserProxy;
import com.yeming.paopao.utils.BitmapUtil;
import com.yeming.paopao.utils.EncryptUtil;
import com.yeming.paopao.utils.FileUtil;
import com.yeming.paopao.utils.LogUtil;
import com.yeming.paopao.views.DeletableEditText;
import com.yeming.paopao.views.ToastView;
import com.yeming.paopao.views.third.CircleImageView;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-20 22:42
 * version: V1.0
 * Description:    登陆
 */
public class LoginActivity extends Activity implements UserProxy.ILoginListener {

    private static final String TAG = "LoginActivity" ;
    final float radius = 8;
    final double scaleFactor = 10;
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
    private CircleImageView userIcon;
    private DeletableEditText userNameInput;
    private DeletableEditText userPwdInput;
    private Button loginButton;
    private TextView registerLink;
    private ImageView backgroundImage;
    private Uri background = null;
    private Context context ;
    private User user ;            // 根据账户名查询的用户
    private UserProxy userProxy ;  // 用户操作代理
    private String userNameStr = null ;
    private Animation animation; // 登录icon 旋转 动画
    private YmApplication ymApplication ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.login_layout);

        context = this ;
        ymApplication = YmApplication.getInstance() ;
        ymApplication.addActivity(this);
        background = (Uri)getIntent().getParcelableExtra("background");

        userProxy = new UserProxy(context) ;

        initView();
        initListener();

    //    LoadStartBackground loadStartBackground = new LoadStartBackground(getApplicationContext()) ;
    //   loadStartBackground.updateStartBackground();
    }

    /**
     *   初始化控件
     */
    public void initView() {

        userIcon = (CircleImageView) findViewById(R.id.userIcon);
        userNameInput = (DeletableEditText) findViewById(R.id.user_name_input);
        userPwdInput = (DeletableEditText) findViewById(R.id.user_pwd_input);
        loginButton = (Button) findViewById(R.id.loginButton);
        registerLink = (TextView) findViewById(R.id.register_link);
        backgroundImage = (ImageView) findViewById(R.id.login_backgroundImage);
        // 默认头像
        //ImageLoader.getInstance().displayImage(null,userIcon,options);

        // 字体风格
        userNameInput.setTypeface(YmApplication.chineseTypeface);
        userPwdInput.setTypeface(YmApplication.chineseTypeface);
        loginButton.setTypeface(YmApplication.chineseTypeface);
        registerLink.setTypeface(YmApplication.chineseTypeface);

        animation = AnimationUtils.loadAnimation(context,R.anim.login_icon) ;
        LinearInterpolator lin = new LinearInterpolator(); // 匀速旋转
        animation.setInterpolator(lin);

        if (background == null) {
            StartBackground startBackground = new LoadStartBackground(this).getStartBackground();
            File file = startBackground.getCacheFile(this);
            if (file.exists()) {
                background = Uri.fromFile(file);
            }
        }

        BitmapDrawable bitmapDrawable;
        if (background == null) {
            bitmapDrawable = BitmapUtil.createBlur(getResources());
        } else {
            String path = FileUtil.getFilePathByUri(background) ;
            bitmapDrawable = BitmapUtil.createBlur(path,getResources());
        }
        backgroundImage.setImageDrawable(bitmapDrawable);
    }

    /**
     *   监听器注册
     */
    public void initListener(){
        loginButton.setOnClickListener(loginButtonClick);
        userPwdInput.setOnFocusChangeListener(userPwdFocus);
        userProxy.setOnLoginListener(this);
        registerLink.setOnClickListener(registerLinkClick);
    }

    /**
     * 登录监听
     */
    View.OnClickListener loginButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String name = userNameInput.getText().toString();
            String password = userPwdInput.getText().toString();

            if(name.isEmpty() && password.isEmpty()){
                userNameInput.setShakeAnimation();
                userPwdInput.setShakeAnimation();
                //Toast.makeText(LoginActivity.this,"请输入账户信息",Toast.LENGTH_SHORT).show();
                ToastView.showToast(context,"请输入账户信息",Toast.LENGTH_SHORT);
                return ;
            }

            if (name.isEmpty()) {
                userNameInput.setShakeAnimation();
            //    Toast.makeText(LoginActivity.this,"邮箱不能为空",Toast.LENGTH_SHORT).show();
                  ToastView.showToast(context,"邮箱不能为空",Toast.LENGTH_SHORT);
                return;
            }

            if (password.isEmpty()) {
                userPwdInput.setShakeAnimation();
            //    Toast.makeText(LoginActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                ToastView.showToast(context,"密码不能为空",Toast.LENGTH_SHORT);
                return;
            }
            LogUtil.d(TAG,"-----name="+name+"====---pwd="+password);
            //  开始登录动画
            if(animation != null){
                userIcon.startAnimation(animation);
            }
            // 加密
            String SHA_password = EncryptUtil.SHA1(password.trim()) ;
            LogUtil.d(TAG,"-------------SHA_password="+SHA_password);
            userProxy.login(name,SHA_password);  // 登录
            //登录时设置不可点击
            registerLink.setClickable(false);
            loginButton.setClickable(false);
        }
    };

    /**
     * 焦点监听器
     */
    View.OnFocusChangeListener userPwdFocus = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                String nameStr = userNameInput.getText().toString() ;
                if (userNameInput.getText().length() == 0 || nameStr.isEmpty()){
                    LogUtil.d(TAG,"-----"+"userName is null!");
                    //userIcon.setBackgroundResource(R.drawable.icon_user_monkey);
                      userIcon.setImageResource(R.drawable.user_icon_default_main);
                }else{
                    if(user != null ){
                        LogUtil.d(TAG,"-----user is not null!");
                        if(nameStr.equals(user.getEmail().toString())){
                            LogUtil.d(TAG,"-----userName is not change!");
                            return ;
                        }
                        LogUtil.d(TAG,"-----userName is change!");
                    }
                    LogUtil.d(TAG,"-----user is null!");
                    //LogUtil.d(TAG,"-----start time="+ Calendar.getInstance().getTimeInMillis());
                    LogUtil.d(TAG,"-----nameStr="+nameStr);
                    BmobQuery<User> query = new BmobQuery<User>() ;
                    query.addWhereEqualTo("username",nameStr) ;
                    query.findObjects(context,new FindListener<User>() {
                        @Override
                        public void onSuccess(List<User> users) {
                            LogUtil.d(TAG,"-------onSuccess---------"+users.size());
                            if(users.size() == 0){
                                user = null ;
                                //userIcon.setBackgroundResource(R.drawable.icon_user_monkey);
                                //ImageLoader.getInstance().displayImage(null,userIcon,options);
                                userIcon.setImageResource(R.drawable.user_icon_default_main);
                                return ;
                            }
                            user = users.get(0) ;
                            if(null == user.getAvatarUrl()){
                                //ImageLoader.getInstance().displayImage(null,userIcon,options);
                                userIcon.setImageResource(R.drawable.user_icon_default_main);
                                return ;
                            }
                        //    String url = user.getAvatar().getFileUrl(context) ;
                            String url = user.getAvatarUrl() ;
                            //LogUtil.d(TAG,"-------avatar url---------"+url);
                            ImageLoader.getInstance().displayImage(url,userIcon,options);
                            //LogUtil.d(TAG,"-----end time="+ Calendar.getInstance().getTimeInMillis());
                        }

                        @Override
                        public void onError(int i, String s) {
                            LogUtil.d(TAG,"-------onError---------"+s);
                            user = null ;
                            //userIcon.setBackgroundResource(R.drawable.icon_user_monkey);
                            //ImageLoader.getInstance().displayImage(null,userIcon,options);
                            userIcon.setImageResource(R.drawable.user_icon_default_main);
                        }
                    });
                }
            }
        }
    } ;

    /**
     * 跳转注册
     */
    View.OnClickListener registerLinkClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent() ;
            intent.setClass(context,RegisterActivity.class) ;
            /*if (background != null) {
                intent.putExtra("background", background);
            }*/
            //startActivity(intent);
            startActivityForResult(intent, Constant.REGISTER_REQUEST_CODE);
        }
    } ;

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            updateLoginButton();
        }
    } ;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.d(TAG,"-------resultCode="+resultCode);
        switch (resultCode){
            case Constant.REGISTER_RESULT_CODE:
                String name = data.getStringExtra("username") ;
                String password = data.getStringExtra("password") ;
                userNameInput.setText(name);
                userPwdInput.setText(password);
                break ;
        }
    }

    /**
     * 登录按钮状态
     */
    public void updateLoginButton(){
        if (userNameInput.getText().length() == 0) {
            loginButton.setEnabled(false);
            return;
        }
        if (userPwdInput.getText().length() == 0) {
            loginButton.setEnabled(false);
            return;
        }
        loginButton.setEnabled(true);
    }

    /**
     * 更新用户的经纬度信息
     */
    /*private void updateUserLocation(){
        if(YmApplication.lastPoint != null){
            String saveLatitude  = ymApplication.getLatitude();
            String saveLongtitude = ymApplication.getLongtitude();
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
                        // TODO Auto-generated method stub
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
    }*/


    /**
     * 登录成功回调
     */
    @Override
    public void onLoginSuccess() {
        LogUtil.d(TAG,"------Login Success-----");
    //    updateUserLocation() ;
        registerLink.setClickable(false);
        loginButton.setClickable(false);
        if(animation != null && animation.hasStarted()){
           // animation.cancel();
            userIcon.clearAnimation();
        }
        Intent intent = new Intent() ;
        intent.setClass(context,MainActivity.class) ;
        startActivity(intent);
        finish();
    }

    /**
     * @param msg
     * 登录失败回调
     */
    @Override
    public void onLoginFailure(int code,String msg) {
        LogUtil.d(TAG,"------Login Failure-----code="+code);
        LogUtil.d(TAG,"------Login Failure-----"+msg);
        registerLink.setClickable(true);
        loginButton.setClickable(true);
        if(code == 101){ //username or password incorrect.
            ToastView.showToast(context,"用户名或密码错误",Toast.LENGTH_SHORT);
        }else if(code == 9016){
            //The network is not available,please check your network!
            ToastView.showToast(context,"请检查网络连接",Toast.LENGTH_SHORT);
        }
        if(animation != null && animation.hasStarted()){
            userIcon.clearAnimation();
        }
    }
}
