package com.yeming.paopao.fmt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.yeming.paopao.R;
import com.yeming.paopao.adapter.DrawerFtmAdapter;
import com.yeming.paopao.app.YmApplication;
import com.yeming.paopao.aty.EditUserInfoActivity;
import com.yeming.paopao.aty.MainActivity;
import com.yeming.paopao.aty.MapLocationActivity;
import com.yeming.paopao.aty.PayActivity;
import com.yeming.paopao.aty.UserPaopaoListActivity;
import com.yeming.paopao.bean.DrawerItemModel;
import com.yeming.paopao.bean.User;
import com.yeming.paopao.commons.Constant;
import com.yeming.paopao.utils.LogUtil;
import com.yeming.paopao.utils.SharedPreHelperUtil;
import com.yeming.paopao.views.ToastView;
import com.yeming.paopao.views.third.CircleImageView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-27 22:11
 * version: V1.0
 * Description:  导航侧边页
 */
@SuppressLint("ValidFragment")
public class DrawerFragment extends Fragment implements View.OnClickListener{

    public static final String TAG = "DrawerFragment" ;
    private MainActivity mainActivity ;
    private ListView list ;
    private View rootView ;
    private DrawerFtmAdapter drawerFtmAdapter ;
    private Typeface englishtypeface, chineseTypeface;

    // 顶部控件
    private CircleImageView userIcon ;
    private TextView userName,userSign ;
    private TextView locationName ;// 所在地名
    private RelativeLayout /*fansLayout,focusLayout,*/user_info_layout ;
    //private TextView fansNum,focusNum ;
    private LinearLayout user_location_layout ;
    private ImageView userSex ;

    //  底部控件
    private LinearLayout firstLayout,secondLayout,threeLayout,fourLayout,fiveLayout ;
    private ImageView leftImage1,leftImage2,leftImage3,leftImage4;
    private TextView centerText1,centerText2,centerText3,centerText4,centerText5;
    private ImageView numberImage1,numberImage2,numberImage3,numberImage4;
    private TextView number1,number2,number3,number4;

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



    public DrawerFragment(MainActivity context){
        this.mainActivity = context ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.drawer_fmt_layout, container,
                false);

        chineseTypeface = YmApplication.chineseTypeface ;
        englishtypeface = YmApplication.englishTypeface ;

        //initListView();
        initHeadView();
        initFootView();
        initLintener();
        setViewData();
        //注册广播
        IntentFilter filter = new IntentFilter() ;
        filter.addAction(Constant.USER_NICK_CHANGE);
        filter.addAction(Constant.USER_SIGN_CHANGE);
        filter.addAction(Constant.USER_AVATER_CHANGE);
        filter.addAction(Constant.USER_SEX_CHANGE);
        filter.addAction(Constant.USER_FANSNUM_CHANGE);
        filter.addAction(Constant.USER_FOCUSNUM_CHANGE);
        filter.addAction(Constant.USER_PAOPAONUM_CHANGE);
        filter.addAction(Constant.USER_LOCATION_CHANGE);
        mainActivity.registerReceiver(broadcastReceiver,filter) ;

        return rootView ;
    }

    private void initView(){

    }

    /**
     * 顶部view
     */
    private void initHeadView(){
        userIcon = (CircleImageView) rootView.findViewById(R.id.left_drawer_userIcon);
        userName = (TextView) rootView.findViewById(R.id.left_drawer_username);
        userSign = (TextView) rootView.findViewById(R.id.left_user_sign) ;
        locationName = (TextView) rootView.findViewById(R.id.left_drawer_userlocation);
        userSex = (ImageView) rootView.findViewById(R.id.user_sex_icon);
    //    fansNum = (TextView) rootView.findViewById(R.id.user_fans_total);
     //   focusNum = (TextView) rootView.findViewById(R.id.user_focus_total);

        user_location_layout = (LinearLayout) rootView.findViewById(R.id.user_location_layout);
        user_info_layout = (RelativeLayout) rootView.findViewById(R.id.user_info_layout);

        userName.setTypeface(chineseTypeface);
        userSign.setTypeface(chineseTypeface);
        locationName.setTypeface(chineseTypeface);
     //   fansNum.setTypeface(chineseTypeface);
     //   focusNum.setTypeface(chineseTypeface);
    }

    /**
     * 底部view
     */
    private void initFootView(){
        //item  layout
        firstLayout = (LinearLayout) rootView.findViewById(R.id.first_layout);
        secondLayout = (LinearLayout) rootView.findViewById(R.id.second_layout);
        threeLayout = (LinearLayout) rootView.findViewById(R.id.three_layout);
        fourLayout = (LinearLayout) rootView.findViewById(R.id.four_layout);
        fiveLayout = (LinearLayout) rootView.findViewById(R.id.five_layout);
        // 左侧图标
        leftImage1 = (ImageView) rootView.findViewById(R.id.item_left_image1);
        leftImage2 = (ImageView) rootView.findViewById(R.id.item_left_image2);
        leftImage3 = (ImageView) rootView.findViewById(R.id.item_left_image3);
        leftImage4 = (ImageView) rootView.findViewById(R.id.item_left_image4);
        // item中间文字
        centerText1 = (TextView) rootView.findViewById(R.id.item_center_text1);
        centerText2 = (TextView) rootView.findViewById(R.id.item_center_text2);
        centerText3 = (TextView) rootView.findViewById(R.id.item_center_text3);
        centerText4 = (TextView) rootView.findViewById(R.id.item_center_text4);
        centerText5 = (TextView) rootView.findViewById(R.id.item_center_text5);
        centerText1.setTypeface(chineseTypeface);
        centerText2.setTypeface(chineseTypeface);
        centerText3.setTypeface(chineseTypeface);
        centerText4.setTypeface(chineseTypeface);
        centerText5.setTypeface(chineseTypeface);
        // 消息数量背景
        numberImage1 = (ImageView) rootView.findViewById(R.id.item_right_image1);
        numberImage2 = (ImageView) rootView.findViewById(R.id.item_right_image2);
        numberImage3 = (ImageView) rootView.findViewById(R.id.item_right_image3);
      //  numberImage4 = (ImageView) rootView.findViewById(R.id.item_right_image4);
        //  消息数量
        number1 = (TextView) rootView.findViewById(R.id.item_right_image_text1);
        number2 = (TextView) rootView.findViewById(R.id.item_right_image_text2);
        number3 = (TextView) rootView.findViewById(R.id.item_right_image_text3);
      //  number4 = (TextView) rootView.findViewById(R.id.item_right_image_text4);
        number1.setTypeface(englishtypeface);
        number2.setTypeface(englishtypeface);
        number3.setTypeface(englishtypeface);
     //   number4.setTypeface(englishtypeface);

        number1.setVisibility(View.INVISIBLE);
        number2.setVisibility(View.INVISIBLE);
        number3.setVisibility(View.INVISIBLE);
      //  number4.setVisibility(View.INVISIBLE);
    }

    /**
     * 设置数据
     */
    public void setViewData(){

        User user =BmobUser.getCurrentUser(mainActivity, User.class);
        String avatarUrl = null;
        if(user != null){
            /*if(user.getAvatar()!=null){
                avatarUrl = user.getAvatar().getFileUrl(mainActivity) ;
            }*/
            avatarUrl = user.getAvatarUrl() ;
            userName.setText(user.getNickname());
            //LogUtil.d(TAG,"-----setViewData location District="+YmApplication.locationDistrict);
            locationName.setText(YmApplication.locationDistrict);
            String s = mainActivity.getResources().getString(R.string.user_sign)+" " ;
            if(user.getSign() == null){
                userSign.setText("");
            }else{
                userSign.setText(s+user.getSign());
            }

            String sex = user.getSex() ;
            if("w".equals(sex)){
                userSex.setBackgroundResource(R.drawable.icon_sex_girl);
            }else{
                userSex.setBackgroundResource(R.drawable.icon_sex_boy);
            }
        }
        ImageLoader.getInstance().displayImage(avatarUrl, userIcon, options);

    //    String fanstr = fansNum.getText().toString() ;
    //    fansNum.setText(String.format(fanstr, SharedPreHelperUtil.getInstance(mainActivity).getUserFansNum()));

    //    String focustr = focusNum.getText().toString() ;
     //   focusNum.setText(String.format(focustr, SharedPreHelperUtil.getInstance(mainActivity).getUserFocusNum()));
    }



    /**
     * 注册监听器
     */
    public void initLintener(){

        user_info_layout.setOnClickListener(this);
        user_location_layout.setOnClickListener(this);
        userIcon.setOnClickListener(this);

        firstLayout.setOnClickListener(this);
        secondLayout.setOnClickListener(this);
        threeLayout.setOnClickListener(this);
        fourLayout.setOnClickListener(this);
        fiveLayout.setOnClickListener(this);

    }

    /*public void initListView(){
        list = (ListView) rootView.findViewById(R.id.left_drawer_list);
        drawerFtmAdapter = new DrawerFtmAdapter(mainActivity,getDrawerItemList()) ;
        list.setAdapter(drawerFtmAdapter);
    }*/

    @Override
    public void onAttach(Activity activity) {
        mainActivity = (MainActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mainActivity.unregisterReceiver(broadcastReceiver);
    }

    private List<DrawerItemModel> getDrawerItemList() {
        List<DrawerItemModel> list=new ArrayList<DrawerItemModel>();
        list.add(new DrawerItemModel(R.drawable.icon_likes, "Views", "26"));
        list.add(new DrawerItemModel(R.drawable.icon_likes, "Likes", "38"));
        list.add(new DrawerItemModel(R.drawable.icon_likes, "Comments","28"));
        list.add(new DrawerItemModel(R.drawable.icon_likes, "Message", "28"));
        list.add(new DrawerItemModel(R.drawable.icon_likes, "Setting", "28"));
        list.add(new DrawerItemModel(R.drawable.icon_likes, "Exit", null));
        return list;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent() ;
        switch (view.getId()){
            case R.id.left_drawer_userIcon:
                intent.setClass(mainActivity, EditUserInfoActivity.class) ;
                startActivity(intent);
                break ;
            case R.id.user_info_layout:
                LogUtil.d(TAG,"------click user_info_layout-----");
                break ;
            case R.id.user_location_layout:
                intent.setClass(mainActivity, MapLocationActivity.class) ;
                startActivity(intent);
                break ;
            case R.id.first_layout:

                break ;
            case R.id.second_layout:
                intent.setClass(mainActivity, UserPaopaoListActivity.class) ;
                intent.putExtra("user",YmApplication.getCurrentUser()) ;
                startActivity(intent);
                break ;
            case R.id.three_layout:
                initFeedbackPreference() ;
                break ;
            case R.id.four_layout:
                intent.setClass(mainActivity, PayActivity.class) ;
                startActivity(intent);
                break ;
            case R.id.five_layout:  //  退出
                mainActivity.getmDrawerLayout().closeDrawers();
            //    mainActivity.onBackPressed();
                YmApplication.getInstance().logout();
                break ;
            default:
                break ;
        }
    }

    /**
     * 发送邮件
     */
    private void initFeedbackPreference(){
        Uri uri = Uri.parse("mailto:yeming_1001@163.com");
        final Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (infos == null || infos.size() <= 0){
            ToastView.showToast(mainActivity,R.string.no_email_tip,Toast.LENGTH_SHORT);
            return;
        }
        startActivity(intent);



    }


    /**
     * 昵称或签名修改完成广播，更新页面数据
     */
     BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction() ;
            LogUtil.d(TAG,"-----action drawer---"+action);
            // 更新签名
            if(action.equals(Constant.USER_SIGN_CHANGE)){
                    User user = BmobUser.getCurrentUser(mainActivity,User.class) ;
                    LogUtil.d(TAG,"-----action USER_SIGN_CHANGE---"+user.getSign());
                    String s = mainActivity.getResources().getString(R.string.user_sign) +" " ;
                if(user.getSign() == null){
                    userSign.setText("");
                }else{
                    userSign.setText(s+user.getSign());
                }
            }else if(action.equals(Constant.USER_NICK_CHANGE)){  //  更新昵称
                        User user = BmobUser.getCurrentUser(mainActivity,User.class) ;
                        LogUtil.d(TAG,"-----action USER_NICK_CHANGE---"+user.getNickname());
                        userName.setText(user.getNickname());
                   //     YmApplication.getInstance().clearCache(null);
            }else if(action.equals(Constant.USER_AVATER_CHANGE)){  // 更新头像
                        User user = BmobUser.getCurrentUser(mainActivity,User.class) ;
                        ImageLoader.getInstance().displayImage(user.getAvatarUrl(),userIcon,options);
                   //     YmApplication.getInstance().clearCache(null);
            }else if(action.equals(Constant.USER_FANSNUM_CHANGE)){   //  跟新粉丝数量
             //           int fansCount = SharedPreHelperUtil.getInstance(mainActivity).getUserFansNum() ;
             //           String fanstr = fansNum.getText().toString() ;
             //           fansNum.setText(String.format(fanstr, fansCount));
            }else if(action.equals(Constant.USER_FOCUSNUM_CHANGE)){   //  更新关注数量
             //           int focusCount = SharedPreHelperUtil.getInstance(mainActivity).getUserFocusNum() ;
            //            String focustr = focusNum.getText().toString() ;
            //            focusNum.setText(String.format(focustr, focusCount));
            }else if(action.equals(Constant.USER_PAOPAONUM_CHANGE)){   //  更新泡泡数量
                        int paopaoNum = SharedPreHelperUtil.getInstance(mainActivity).getUserPaopaoNum() ;
                        if(paopaoNum != 0){
                            number2.setText(paopaoNum+"");
                            number2.setVisibility(View.VISIBLE);
                        }
            }else if(action.equals(Constant.USER_SEX_CHANGE)){    //  更新性别图标
                User user = BmobUser.getCurrentUser(mainActivity,User.class) ;
                if("w".equals(user.getSex())){
                    userSex.setBackgroundResource(R.drawable.icon_sex_girl);
                }else{
                    userSex.setBackgroundResource(R.drawable.icon_sex_boy);
                }
            }else if(action.equals(Constant.USER_LOCATION_CHANGE)){    //  位置更新
                LogUtil.d(TAG,"-----action USER_LOCATION_CHANGE---"+intent.getStringExtra("location"));
                String location = intent.getStringExtra("location") ;
                if(location != null){
                    locationName.setText(location+"");
                }

            }
        }
    } ;


}
