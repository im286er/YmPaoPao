package com.yeming.paopao.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-02-04 22:46
 * version: V1.0
 * Description: 泡泡
 */
public class Paopao extends BmobObject{
    /* paopao 内容  */
    private String content ;
    /* 泡泡 配图  */
 // private BmobFile image ;
    /* 泡泡 配图 url     替换文件管理类为新版的   只需存文件url即可 */
    private String imageUrl ;
    /* 发布者  */
    private User user ;
    /* 泡泡 评论  */
    private BmobRelation comment ;
    /* 点赞总数  */
 // private Number likes ;
    /* 点赞的人  */
    private BmobRelation likers ;
    /* 来自设备 名称  */
    private String device ;
    /* 发布时间  */
    private String createTimeMillis ;

    /* 点赞总数  */
    private Number likes ;
    /* 评论总数  */
    private Number comments ;
    /* 分享总数  */
    private Number shares ;


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BmobRelation getComment() {
        return comment;
    }

    public void setComment(BmobRelation comment) {
        this.comment = comment;
    }

    public BmobRelation getLikers() {
        return likers;
    }

    public void setLikers(BmobRelation likers) {
        this.likers = likers;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }


    public String getCreateTimeMillis() {
        return createTimeMillis;
    }

    public void setCreateTimeMillis(String createTimeMillis) {
        this.createTimeMillis = createTimeMillis;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
