package com.yeming.paopao.aty;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yeming.paopao.R;
import com.yeming.paopao.app.YmApplication;
import com.yeming.paopao.bean.User;
import com.yeming.paopao.utils.LogUtil;
import com.yeming.paopao.views.third.DialogView;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-28 21:27
 * version: V1.0
 * Description:    签名编辑页面
 */
public class EditUserSignActivity extends Activity {

    private String TAG = "EditUserSignActivity" ;
    private Context context ;
    private EditText editText ;
    private Dialog saveDialog ,loadDialog;
    private User user = null ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.edit_user_sign_layout);

        YmApplication.getInstance().addActivity(this);
        context = this ;
        setActionBar();
        user = BmobUser.getCurrentUser(context, User.class);
        initView() ;
    }

    private void initView(){

        loadDialog = DialogView.loadDialog(context,R.string.saveing) ;
        editText = (EditText) findViewById(R.id.edit_sign);
        editText.setTypeface(YmApplication.chineseTypeface);

        editText.setText(user.getSign());

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
        int titleId = Resources.getSystem().getIdentifier("action_bar_title",
                "id", "android");
        TextView textView = (TextView) findViewById(titleId);
        textView.setTypeface(YmApplication.chineseTypeface);
        textView.setTextColor(0xFFdfdfdf);
        textView.setTextSize(20);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId() ;
        switch (id){
            case android.R.id.home:
               /* String sign = editText.getText().toString() ;
                LogUtil.d(TAG,"-------sign-------"+sign);
                *//*if(user.getSign().equals(sign)){
                    LogUtil.d(TAG,"-------sign-------"+sign);
                    super.onBackPressed();
                }else{
                    showSaveTipDialog();
                }*//*
                showSaveTipDialog();*/
                onBackPressed();
                break ;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onBackPressed() {
        String sign = editText.getText().toString() ;
        LogUtil.d(TAG,"------sign---"+sign);
        LogUtil.d(TAG,"------sign---"+user.getSign());
        if("".equals(sign.trim())){
            super.onBackPressed();
        }else if(sign.equals(user.getSign())){
            super.onBackPressed();
        }else{
            showSaveTipDialog();
        }
    }

    private void backPressed(){
        super.onBackPressed(); ;
    }

    /**
     * 更新签名
     * @param sign
     */
    private void updateSign(final String sign){
        User user = BmobUser.getCurrentUser(context, User.class);
        User newUser = new User() ;
        newUser.setSign(sign);
        newUser.update(context,user.getObjectId(),new UpdateListener() {
            @Override
            public void onSuccess() {
                LogUtil.i(TAG, "更新信息成功。");
                loadDialog.dismiss();
                Intent intent = new Intent() ;
                intent.setClass(context,EditUserInfoActivity.class) ;
                intent.putExtra("sign",sign) ;
                EditUserSignActivity.this.setResult(Activity.RESULT_OK, intent);
                EditUserSignActivity.this.finish();
            }

            @Override
            public void onFailure(int i, String s) {
                LogUtil.i(TAG, "签名更新失败-->code=" + i + "_" + s);
                loadDialog.dismiss();
            }
        });
    }

    /**
     * 是否保存提框
     */
    private void showSaveTipDialog(){
        // editDialog = new AlertDialog.Builder(context).create();
        saveDialog = new Dialog(context,R.style.mydialog) ;
        saveDialog.setCanceledOnTouchOutside(false);
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_save_tips_layout, null);
        // editDialog.getWindow().setContentView(R.layout.edit_dialog_layout);
        saveDialog.getWindow().setContentView(v);

        TextView titleTip = (TextView) v.findViewById(R.id.titleTip);
        Button setCancle = (Button) v.findViewById(R.id.set_cancle);
        Button setSave = (Button) v.findViewById(R.id.set_save);

        titleTip.setTypeface(YmApplication.chineseTypeface);
        setCancle.setTypeface(YmApplication.chineseTypeface);
        setSave.setTypeface(YmApplication.chineseTypeface);

        setCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDialog.dismiss();
                /*Intent intent = new Intent() ;
                intent.setClass(context,EditUserInfoActivity.class) ;
                EditUserSignActivity.this.startActivity(intent);
                EditUserSignActivity.this.finish();*/
                backPressed();

            }
        });
        setSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDialog.dismiss();
                loadDialog.show();
                // 更新签名
                String sign = editText.getText().toString() ;
                updateSign(sign);

            }
        });

        saveDialog.getWindow().setGravity(Gravity.CENTER);
        saveDialog.show();
        //设置自定义高度，在布局edit_dialog_layout中设置layout_width宽度无效。需使用下面代码设置dialog宽度
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = saveDialog.getWindow().getAttributes();
        lp.width = (int)(display.getWidth() - 80); //设置宽度
        saveDialog.getWindow().setAttributes(lp);
    }
}
