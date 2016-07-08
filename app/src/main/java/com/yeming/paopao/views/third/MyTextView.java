package com.yeming.paopao.views.third;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-28 21:27
 * version: V1.0
 * Description:
 */
public class MyTextView extends TextView {

    private Context context ;

    public MyTextView(Context context) {
        super(context);
        this.context = context ;
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context ;
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context ;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Layout layout = getLayout();
        if (layout != null) {
            int height = (int) FloatMath.ceil(getMaxLineHeight(this.getText().toString()))
                    + getCompoundPaddingTop() + getCompoundPaddingBottom();
            int width = getMeasuredWidth();
            setMeasuredDimension(width, height);
        }
    }

    private float getMaxLineHeight(String str) {
        float height = 0.0f;
        float screenW = ((Activity)context).getWindowManager().getDefaultDisplay().getWidth();
        float paddingLeft = ((RelativeLayout)this.getParent()).getPaddingLeft();
        float paddingReft = ((RelativeLayout)this.getParent()).getPaddingRight();
        //这里具体this.getPaint()要注意使用，要看你的TextView在什么位置，这个是拿TextView父控件的Padding的，为了更准确的算出换行
        int line = (int) Math.ceil( (this.getPaint().measureText(str)/(screenW-paddingLeft-paddingReft)));
        height = (this.getPaint().getFontMetrics().descent-this.getPaint().getFontMetrics().ascent)*line;
        return height;
    }
}
