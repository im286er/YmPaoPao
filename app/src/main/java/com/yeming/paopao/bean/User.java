package com.yeming.paopao.bean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-19 22:41
 * version: V1.0
 * Description:  用户
 */
public class User extends BmobUser {

    private static final long serialVersionUID = 1L;
    /* 头像 */
    //private BmobFile avatar;
    // 头像链接
    private String avatarUrl ;
    /* 标签 */
 // private String tags;
    /* 昵称 */
    private String nickname;
    /* 个性签名 */
    private String sign;
    /* 个人说明 */
 // private String explanation;
    /* 性别   m or w */
    private String sex;
    /* 生日 */
 // private String birthday;
    /* 地理位置 所在地 */
 // private String location;
    /* 地理位置 */
    private BmobGeoPoint locationPoint;
    /* 数据拼音的首字母 */
 // private String sortLetters;
    /* 关联设备 */
 // private BmobRelation installation;
    /* 粉丝 */
    private BmobRelation fans ;
    /* 所关注人 */
    private BmobRelation focus ;
    /* 所发布的泡泡 */
    private BmobRelation paopao ;

    private BmobRelation comment ;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public BmobGeoPoint getLocationPoint() {
        return locationPoint;
    }

    public void setLocationPoint(BmobGeoPoint locationPoint) {
        this.locationPoint = locationPoint;
    }

    public BmobRelation getFans() {
        return fans;
    }

    public void setFans(BmobRelation fans) {
        this.fans = fans;
    }

    public BmobRelation getFocus() {
        return focus;
    }

    public void setFocus(BmobRelation focus) {
        this.focus = focus;
    }

    public BmobRelation getPaopao() {
        return paopao;
    }

    public void setPaopao(BmobRelation paopao) {
        this.paopao = paopao;
    }

    public BmobRelation getComment() {
        return comment;
    }

    public void setComment(BmobRelation comment) {
        this.comment = comment;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
