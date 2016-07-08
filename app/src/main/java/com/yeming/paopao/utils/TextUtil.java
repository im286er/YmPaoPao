package com.yeming.paopao.utils;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-26 23:03
 * version: V1.0
 * Description:  String 校验与处理
 */
public class TextUtil {

    /**
     * @param str
     * @return boolean
     * 校验邮箱格式
     */
    public static Boolean isEmail(String str) {
        Boolean isEmail = false;
        String expr = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        if (str.matches(expr)) {
            isEmail = true;
        }
        return isEmail;
    }

    /**
     * @param target
     * @return 校验邮箱格式
     */
    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
                    .matches();
        }
    }

}
