package com.yeming.paopao.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yeming.paopao.R;
import com.yeming.paopao.bean.PicViewImage;
import com.yeming.paopao.commons.Constant;
import com.yeming.paopao.utils.ImageLoadOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-02-02 21:12
 * version: V1.0
 * Description: ViewPage 适配器
 */
public class ViewPageAdapter extends PagerAdapter {

    private List<ImageView> imageViews;
    private Activity mActivity;
    private List<PicViewImage> list ;
    private ImageLoader imageLoader ;
    private DisplayImageOptions options;

    public ViewPageAdapter(List<PicViewImage> list, Activity activity){
        this.list = list ;
        this.mActivity = activity;
        imageViews = new ArrayList<ImageView>();
        imageLoader = ImageLoader.getInstance() ;
        for (int i = 0; i < Constant.VIEWPAGER_SIZE; i++) {
            ImageView imageView = new ImageView(activity);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageViews.add(imageView);
        }
        this.options = ImageLoadOptions.getOptionsForPager() ;
    }

    public void setList(List<PicViewImage> list){
        this.list = list ;
    }

    @Override
    public int getCount() {
        return Constant.VIEWPAGER_SIZE;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o ;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = imageViews.get(position);
        if(list.size() == 0){
            imageView.setBackgroundResource(R.drawable.default_load_bg) ;
        }else{
            imageLoader.displayImage(list.get(position).getViewImage().getFileUrl(mActivity),imageView,options);
        }
        ((ViewPager) container).addView(imageView);
        return imageViews.get(position);
    }
}
