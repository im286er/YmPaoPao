package com.yeming.paopao.bean;

import android.content.Context;

import com.yeming.paopao.app.YmApplication;

import java.io.File;
import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-24 10:24
 * version: V1.0
 * Description:  启动页背景图片
 */
public class StartBackground extends BmobObject implements Serializable {

    private static final long serialVersionUID = 1L;
    /* 图片 */
    private BmobFile startImage;
    /* 图片标题 */
    private String imageTips;
    /* 图片标签 */
    private String imageTag;

    public BmobFile getStartImage() {
        return startImage;
    }

    public void setStartImage(BmobFile startImage) {
        this.startImage = startImage;
    }

    public String getImageTips() {
        return imageTips;
    }

    public void setImageTips(String imageTips) {
        this.imageTips = imageTips;
    }

    public String getImageTag() {
        return imageTag;
    }

    public void setImageTag(String imageTag) {
        this.imageTag = imageTag;
    }

    /**
     * @return
     */
    public String getCacheImageName() {
        return getObjectId();
    }

    /**
     * @param ctx
     * @return
     */
    public File getCacheFile(Context ctx) {
        File file;
        if (getCacheImageName() != null) {
            file = new File(getStartImageDir(ctx), getCacheImageName());
        } else {
            file = new File(getStartImageDir(ctx), String.valueOf(-1));
        }
        return file;
    }

    /**
     * @param ctx
     * @return
     */
    public boolean isImageCached(Context ctx) {
        return getCacheFile(ctx).exists();
    }

    private File getStartImageDir(Context ctx) {
        final String dirName = "START_BACKGROUND";
        File root = ctx.getExternalFilesDir(null);
        File dir = new File(root, dirName);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdir();
        }/*else{
            YmApplication.getInstance().clearCache(dir);
            dir.mkdir() ;
        }*/
        return dir;
    }
}
