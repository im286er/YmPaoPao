package com.yeming.paopao.bean;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-30 21:13
 * version: V1.0
 * Description:  左侧导航栏 Model
 */
public class DrawerItemModel {

    private int leftDrawbleResource;
    private String centerTitle;
    private String number;

    public DrawerItemModel() {
    }

    public DrawerItemModel(int leftDrawbleResource, String centerTitle, String number) {
        this.centerTitle = centerTitle;
        this.leftDrawbleResource = leftDrawbleResource;
        this.number = number;
    }

    public int getLeftDrawbleResource() {
        return leftDrawbleResource;
    }

    public void setLeftDrawbleResource(int leftDrawbleResource) {
        this.leftDrawbleResource = leftDrawbleResource;
    }

    public String getCenterTitle() {
        return centerTitle;
    }

    public void setCenterTitle(String centerTitle) {
        this.centerTitle = centerTitle;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
