package com.yeming.paopao.views;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-02-02 21:10
 * version: V1.0
 * Description:  viewpage 滑动速度
 */
public class FixedSpeedScroller extends Scroller {
    public FixedSpeedScroller(Context context, Interpolator interpolator,
                              boolean flywheel) {
        super(context, interpolator, flywheel);
        // TODO Auto-generated constructor stub
    }

    public FixedSpeedScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
        // TODO Auto-generated constructor stub
    }

    private int mDuration = 800;

    public FixedSpeedScroller(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }
}
