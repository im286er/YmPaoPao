package com.yeming.paopao.bean;

import android.content.Context;

import cn.bmob.v3.BmobInstallation;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-19 22:43
 * version: V1.0
 * Description:
 */
public class _Installation extends BmobInstallation {

    private User user;

    public _Installation(Context context) {
        super(context);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
