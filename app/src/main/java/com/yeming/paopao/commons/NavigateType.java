package com.yeming.paopao.commons;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-28 21:06
 * version: V1.0
 * Description:  导航分类
 */
public enum NavigateType {

    // 主页
    HOME {
        @Override
        public int getValue() {
            return 0;
        }
    };

    public abstract int getValue();
}
