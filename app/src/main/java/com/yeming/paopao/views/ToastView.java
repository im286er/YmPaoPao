package com.yeming.paopao.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yeming.paopao.R;
import com.yeming.paopao.app.YmApplication;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-27 20:39
 * version: V1.0
 * Description:   通用Toast
 */
public class ToastView {

    private static Toast toast = null;
    private static TextView textView ;

    public static void showToast(Context context,int textId,int showTime){
        if(toast == null){
            LayoutInflater inflater = LayoutInflater.from(context) ;
            View view = inflater.inflate(R.layout.toast_layout,null) ;
            textView = (TextView) view.findViewById(R.id.toast_text);
            textView.setText(textId);
            textView.setTypeface(YmApplication.chineseTypeface);
            toast = new Toast(context) ;
            toast.setView(view);
            toast.setDuration(showTime);
         //   toast.setGravity(Gravity.BOTTOM,0,50);
        }else{
            textView.setText(textId);
        }
        toast.show();
    }

    public static void showToast(Context context,String str,int showTime){
        if(toast == null){
            LayoutInflater inflater = LayoutInflater.from(context) ;
            View view = inflater.inflate(R.layout.toast_layout,null) ;
            textView = (TextView) view.findViewById(R.id.toast_text);
            textView.setText(str);
            textView.setTypeface(YmApplication.chineseTypeface);
            toast = new Toast(context) ;
            toast.setView(view);
            toast.setDuration(showTime);
          //  toast.setGravity(Gravity.BOTTOM,0,50);
        }else{
            textView.setText(str);
        }
        toast.show();
    }
}
