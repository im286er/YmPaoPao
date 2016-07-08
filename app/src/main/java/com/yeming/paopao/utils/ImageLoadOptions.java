package com.yeming.paopao.utils;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.yeming.paopao.R;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-24 10:07
 * version: V1.0
 * Description:
 */
public class ImageLoadOptions {

    public static DisplayImageOptions getOptionsForNormal() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                // // 设置图片在下载期间显示的图片
                .showImageOnLoading(R.drawable.default_load_bg)
                        // // 设置图片Uri为空或是错误的时候显示的图片
                .showImageForEmptyUri(R.drawable.default_fail_bg)
                        // // 设置图片加载/解码过程中错误时候显示的图片
                .showImageOnFail(R.drawable.default_fail_bg)
                .cacheInMemory(true)
                        // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)
                        // 设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY)// 设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
                        // .decodingOptions(android.graphics.BitmapFactory.Options
                        // decodingOptions)//设置图片的解码配置
                .considerExifParams(true)
                        // 设置图片下载前的延迟
                        // .delayBeforeLoading(int delayInMillis)//int
                        // delayInMillis为你设置的延迟时间
                        // 设置图片加入缓存前，对bitmap进行设置
                        // 。preProcessor(BitmapProcessor preProcessor)
                .resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
                        // .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
                .displayer(new FadeInBitmapDisplayer(400))// 淡入
                .build();

        return options;
    }


    public static DisplayImageOptions getOptionsById(int defaultDrawable) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(defaultDrawable)
                .showImageForEmptyUri(defaultDrawable)
                .showImageOnFail(defaultDrawable)
                .cacheInMemory(true)
                .cacheOnDisc(true).considerExifParams(false)
                .displayer(new FadeInBitmapDisplayer(400))
                .bitmapConfig(Bitmap.Config.ALPHA_8).build();
        return options;
    }

    /**
     * @return ViewPage options
     */
    public static DisplayImageOptions getOptionsForPager() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_load_bg)
                .showImageForEmptyUri(R.drawable.default_fail_bg)
                .showImageOnFail(R.drawable.default_fail_bg)
                .cacheInMemory(true)
                .cacheOnDisc(true).considerExifParams(false)
                .displayer(new FadeInBitmapDisplayer(400))
                .bitmapConfig(Bitmap.Config.ALPHA_8).build();
        return options;
    }

}
