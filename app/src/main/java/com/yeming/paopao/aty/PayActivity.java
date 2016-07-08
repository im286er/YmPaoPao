package com.yeming.paopao.aty;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.yeming.paopao.R;
import com.yeming.paopao.app.YmApplication;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-08-08 16:13
 * version: V1.0
 * Description:
 */
public class PayActivity extends Activity {

    private TextView tip,tip1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payactivity);
        setActionBar() ;
        initView() ;
    }

    private void initView(){
        tip = (TextView) findViewById(R.id.tip);
        tip1 = (TextView) findViewById(R.id.tip1);
        tip.setTypeface(YmApplication.chineseTypeface);
        tip1.setTypeface(YmApplication.chineseTypeface);
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
                onBackPressed();
                break ;
        }
        return super.onMenuItemSelected(featureId, item);
    }

}
