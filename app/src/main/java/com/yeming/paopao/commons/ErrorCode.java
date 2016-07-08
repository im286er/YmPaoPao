package com.yeming.paopao.commons;


/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-26 20:28
 * version: V1.0
 * Description:
 */
public enum ErrorCode {

    UserName_Pwd_error {
        @Override
        public int getValue() {
            return 101;
        }
    },

    network_error {
        @Override
        public int getValue() {
            return 9016;
        }
    };

    public abstract int getValue();
}
