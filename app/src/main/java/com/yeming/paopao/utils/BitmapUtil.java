package com.yeming.paopao.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.yeming.paopao.R;
import com.yeming.paopao.app.YmApplication;
import com.yeming.paopao.views.third.FastBlur;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-26 21:34
 * version: V1.0
 * Description:   图片处理
 */
public class BitmapUtil {

    static final float radius = 8;
    static final double scaleFactor = 10;

    /**
     * @param resources
     * @return 快速模糊图片
     */
    public static BitmapDrawable createBlur(Resources resources) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, R.drawable.entrance1, options);
        int height = options.outHeight;
        int width = options.outWidth;

        options.outHeight = (int) (height / scaleFactor);
        options.outWidth = (int) (width / scaleFactor);
        options.inSampleSize = (int) (scaleFactor + 0.5);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inMutable = true;

        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.entrance1, options);
        Bitmap blurBitmap = FastBlur.doBlur(bitmap, (int) radius, true);

        return new BitmapDrawable(resources, blurBitmap);
    }

    /**
     * @param path
     * @param resources
     * @return 快速模糊图片
     */
    public static BitmapDrawable createBlur(String path, Resources resources) {
        //  String path = getFilePathByUri(uri);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int height = options.outHeight;
        int width = options.outWidth;

        options.outHeight = (int) (height / scaleFactor);
        options.outWidth = (int) (width / scaleFactor);
        options.inSampleSize = (int) (scaleFactor + 0.5);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inMutable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(path, options);

        Bitmap blurBitmap = FastBlur.doBlur(bitmap, (int) radius, true);

        return new BitmapDrawable(resources, blurBitmap);
    }

    public static double getPicScale(Resources res, int id) {
        // TODO Auto-generated method stub
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeResource(res, id, options);
        double height = options.outHeight;
        double width = options.outWidth;
        return height / width;
    }

    /**
     * 获取屏幕宽高
     * @return
     */
    public static int[] getScreenSize() {
        int[] screens;
        // if (Constants.screenWidth > 0) {
        // return screens;
        // }
        DisplayMetrics dm=new DisplayMetrics();
        dm= YmApplication.getInstance().getResources().getDisplayMetrics();
        screens=new int[]{dm.widthPixels, dm.heightPixels};
        return screens;
    }

    /**
     * @param bitmap
     * @param imageView
     * @param screenRadio
     * @return
     *
     * 获取缩放后的图片宽高
     */
    public static float[] getBitmapConfiguration(Bitmap bitmap, ImageView imageView, float screenRadio) {
        int screenWidth=getScreenSize()[0];
        float rawWidth=0;
        float rawHeight=0;
        float width=0;
        float height=0;
        if(bitmap == null) {
            // rawWidth = sourceWidth;
            // rawHeight = sourceHeigth;
            width=(float)(screenWidth / screenRadio);
            height=(float)width;
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            rawWidth=bitmap.getWidth();
            rawHeight=bitmap.getHeight();
            if(rawHeight > 10 * rawWidth) {
                imageView.setScaleType(ImageView.ScaleType.CENTER);
            } else {
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            }
            float radio=rawHeight / rawWidth;

            width=(float)(screenWidth / screenRadio);
            height=(float)(radio * width);
        }
        return new float[]{width, height};
    }
}
