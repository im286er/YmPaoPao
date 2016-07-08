package com.yeming.paopao.bean;

import cn.bmob.v3.BmobObject;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-28 21:27
 * version: V1.0
 * Description:  评论
 */
public class Comment extends BmobObject{

    /*  评论内容*/
    private String content ;
    /* 用户*/
    private User user ;
    /*  评论的泡泡*/
    private Paopao paopao ;


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

    public Paopao getPaopao() {
        return paopao;
    }

    public void setPaopao(Paopao paopao) {
        this.paopao = paopao;
    }
}
