package com.yeming.paopao.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-02-02 21:36
 * version: V1.0
 * Description:  ViewPage显示的图片
 */
public class PicViewImage extends BmobObject {

    /* 图片 */
    private BmobFile ViewImage;
    /* 图片标题 */
    private String imageTips;
    /* 图片标签 */
    private String imageTag;


    public BmobFile getViewImage() {
        return ViewImage;
    }

    public void setViewImage(BmobFile viewImage) {
        ViewImage = viewImage;
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
}
