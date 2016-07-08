package com.yeming.paopao.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-02-03 21:01
 * version: V1.0
 * Description:  图片 瀑布流
 */
public class Pictrue extends BmobObject{

    /* 图片名称  */
    private String imageName ;
    /* 图片配文  说明  */
    private String imageTips ;
    /* 图片标签  */
    private String imageTags ;
    /* 喜爱总数  */
    private Number likes ;
    /* 图片拥有者  */
    private User user ;
    /* 图片 */
    private BmobFile picture ;
    /* 喜爱图片的用户 */
    private BmobRelation likers ;

    public String getImageTips() {
        return imageTips;
    }

    public void setImageTips(String imageTips) {
        this.imageTips = imageTips;
    }

    public String getImageTags() {
        return imageTags;
    }

    public void setImageTags(String imageTags) {
        this.imageTags = imageTags;
    }

    public Number getLikes() {
        return likes;
    }

    public void setLikes(Number likes) {
        this.likes = likes;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BmobFile getPicture() {
        return picture;
    }

    public void setPicture(BmobFile picture) {
        this.picture = picture;
    }

    public BmobRelation getLikers() {
        return likers;
    }

    public void setLikers(BmobRelation likers) {
        this.likers = likers;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
