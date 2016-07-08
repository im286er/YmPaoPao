package com.yeming.paopao.commons;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-19 21:44
 * version: V1.0
 * Description:    常量
 */
public class Constant {

    public static final String USER_NICK_CHANGE = "USER_NICK_CHANGE" ;  //昵称修改广播
    public static final String USER_SIGN_CHANGE = "USER_SIGN_CHANGE" ;  //签名修改广播
    public static final String USER_AVATER_CHANGE = "USER_AVATER_CHANGE" ;  //头像修改广播
    public static final String USER_FANSNUM_CHANGE = "USER_FANSNUM_CHANGE" ;  //粉丝数修改广播
    public static final String USER_FOCUSNUM_CHANGE = "USER_FOCUSNUM_CHANGE" ;  //关注数修改广播
    public static final String USER_PAOPAONUM_CHANGE = "USER_PAOPAONUM_CHANGE" ;  //泡泡数修改广播
    public static final String USER_LOCATION_CHANGE = "USER_LOCATION_CHANGE" ;  //位置修改广播
    public static final String USER_SEX_CHANGE = "USER_SEX_CHANGE" ;  //性别修改广播
    public static final String PREF_LATITUDE = "latitude";     // 维度
    public static final String PREF_LONGTITUDE = "longtitude"; // 经度
    public static final String FANS_NUM = "FANS_NUM";     // 粉丝数
    public static final String FOCUS_NUM = "FOCUS_NUM"; // 关注数
    public static final String PAOPAO_NUM = "PAOPAO_NUM"; // 泡泡数
    /* bmob app id */
    public static final String APP_ID = "b798583a69348f6906b320fcad29f97e";
    /* bmob app ACCESS_KEY */
    public static final String ACCESS_KEY = "1ba189232754b48960543485bb2c18d4" ;
    /*性别 男*/
    public static final String SEX_MALE = "m";
    /*性别 女*/
    public static final String SEX_FEMALE = "w";
    /*头部 viewpage 图片数量*/
    public static final int VIEWPAGER_SIZE = 5;
    /*    每页加载评论数量*/
    public static final int COMMENT_PAGE_SIZE = 5;
    /*每页图片数量   pageSize*/
    public static final int PIC_PAGE_SIZE = 20;
    /*每页数量   pageSize*/
    public static final int PAGE_SIZE = 10;  //  15
    /*ImageLoad 缓存的目录地址*/
    public static final String OwnCacheDirectory = "ymPaoPao/Cache";
    /*  数据缓存 目录，object */
    public static final String DATA_CACHE_FILDER = "data_cache";
    /*  数据缓存 文件，StartBackground object */
    public static final String START_BACKGROUNDS = "START_BACKGROUNDS";
    /*  数据缓存 文件，Pictrue object */
    public static final String GRID_PICTRUES = "GRID_PICTRUES";
    /*  数据缓存 文件，All Paopao object */
    public static final String LIST_PAOPAOS = "LIST_PAOPAOS";
    /*  数据缓存 文件，FRIEND Paopao object */
    public static final String FRIEND_LIST_PAOPAOS = "FRIEND_LIST_PAOPAOS";
    /*  数据缓存 文件，ViewPage object */
    public static final String VIEWPAGE_PICTRUES = "VIEWPAGE_PICTRUES";
    /*  数据缓存 文件，用户粉丝列表 */
    public static final String USER_FANS_LIST = "USER_FANS_LIST";
    /*  数据缓存 文件，用户关注列表 */
    public static final String USER_FOCUS_LIST = "USER_FOCUS_LIST";
    /*  SharedPreference 文件名*/
    public static final String GLOBAL_SETTING = "GLOBAL_SETTING";
    /*  SharedPreference 启动页背景图更新时间*/
    public static final String START_BACKGROUND_SETTING_TIME = "START_BACKGROUND_SETTING_TIME";
    /*  登录页面跳转注册页面  请求码 */
    public static final int REGISTER_REQUEST_CODE = 1;
    /*  注册页面跳转登录页面  结果码 */
    public static final int REGISTER_RESULT_CODE = 2;
    /*  用户信息页面跳转编辑签名页面  请求码 */
    public static final int EDIT_SIGN_REQUEST_CODE = 3;
    /*  泡泡页面点击头像跳转用户信息页面  请求码 */
    public static final int PAOPAO_USER_ICON_REQUEST_CODE = 3;

}
