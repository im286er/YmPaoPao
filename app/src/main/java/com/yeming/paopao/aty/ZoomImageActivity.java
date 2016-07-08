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
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.yeming.paopao.R;
import com.yeming.paopao.app.YmApplication;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-28 21:27
 * version: V1.0
 * Description:    图片显示   缩放
 */
public class ZoomImageActivity extends Activity{

    private ImageView mImageView;
    private PhotoViewAttacher mAttacher;
    private String url = "" ;
    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.default_load_bg)
            .showImageOnFail(R.drawable.default_fail_bg)
            .resetViewBeforeLoading(true)
            .cacheOnDisc(false)
            .imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .considerExifParams(true)
            .displayer(new FadeInBitmapDisplayer(300))
            .build();
    private ImageLoader imageLoader = ImageLoader.getInstance() ;
    private Context context ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.zoom_image_layout);

        YmApplication.getInstance().addActivity(this);
        context = this ;
        setActionBar();

        Intent intent = getIntent() ;
        url = intent.getStringExtra("url") ;

        mImageView = (ImageView) findViewById(R.id.iv_photo);
     //   mAttacher = new PhotoViewAttacher(mImageView);
        imageLoader.displayImage(url,mImageView,options);
    //    mAttacher.update();
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
        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setTitle("");
        int titleId = Resources.getSystem().getIdentifier("action_bar_title",
                "id", "android");
        TextView textView = (TextView) findViewById(titleId);
        textView.setTypeface(YmApplication.chineseTypeface);
        textView.setTextColor(0xFFdfdfdf);
        textView.setTextSize(20);
    }

    /*@Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId() ;
        switch (id){
            case android.R.id.home:
                onBackPressed();
                break ;
        }
        return super.onMenuItemSelected(featureId, item);
    }*/
}
