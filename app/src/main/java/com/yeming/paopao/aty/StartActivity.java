package com.yeming.paopao.aty;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.yeming.paopao.R;
import com.yeming.paopao.app.YmApplication;
import com.yeming.paopao.bean.StartBackground;
import com.yeming.paopao.bean.User;
import com.yeming.paopao.commons.Constant;
import com.yeming.paopao.commons.LoadStartBackground;
import com.yeming.paopao.utils.LogUtil;

import java.io.File;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-24 11:37
 * version: V1.0
 * Description:
 */
public class StartActivity extends Activity {

    private static final String TAG = "StartActivity" ;
    private ImageView backgroundImage;
    private TextView imageTips;
    private Animation animation;
    private Uri background = null;
    private TextView title ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.start_layout);

        YmApplication.getInstance().addActivity(this);
        Bmob.initialize(getApplicationContext(), Constant.APP_ID);
        initView();
    }

    public void initView() {

    //    title = (TextView) findViewById(R.id.title) ;
    //    title.setTypeface(YmApplication.chineseTypeface);
        backgroundImage = (ImageView) findViewById(R.id.backgroundImage);
        imageTips = (TextView) findViewById(R.id.imageTips);
        imageTips.setTypeface(YmApplication.chineseTypeface);
        animation = AnimationUtils.loadAnimation(this, R.anim.entrance);
        /* 设置背景*/
        StartBackground startBackground = new LoadStartBackground(this).getStartBackground();
        File file = startBackground.getCacheFile(this);
        if (file.exists()) {
            background = Uri.fromFile(file);
            backgroundImage.setImageURI(background);
            imageTips.setText(startBackground.getImageTips());
        }
        // 背景动画
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                next();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        backgroundImage.startAnimation(animation);
    }

    /**
     * 跳转
     */
    public void next() {
        Intent intent;
        User user = BmobUser.getCurrentUser(this, User.class);
        if (user == null) {
            intent = new Intent(this, LoginActivity.class);
            if (background != null) {
                intent.putExtra("background", background);
            }
        } else {
            LogUtil.d(TAG,"----------"+user.getUsername());
            intent = new Intent(this, MainActivity.class);
            //intent = new Intent(this, EditPaopaoActivity.class);
        }
        startActivity(intent);
        this.finish();
    }
}
