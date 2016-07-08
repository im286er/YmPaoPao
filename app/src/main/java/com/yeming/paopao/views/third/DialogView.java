package com.yeming.paopao.views.third;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yeming.paopao.R;
import com.yeming.paopao.app.YmApplication;

import static com.yeming.paopao.R.anim.loading;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-28 21:27
 * version: V1.0
 * Description:   自定义提示框
 */
@SuppressWarnings("ALL")
public class DialogView {

    /**
     * @param context
     * @retur  Dialog
     * @Description: 自定义Dialog
     */
    public static Dialog loadDialog(Context context,int resId){
        //	Dialog dialog = new Dialog(this, R.style.dialog) ;
        Dialog dialog = new Dialog(context, R.style.mydialog) ;
        View view = LayoutInflater.from(context).inflate(R.layout.loading_dialog_layout, null) ;
        //	dialog.setContentView(R.layout.dialog) ;
        dialog.setContentView(view) ;
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView1) ;
        TextView textView = (TextView) view.findViewById(R.id.dialog_msg) ;
        textView.setText(resId) ;
        textView.setTypeface(YmApplication.chineseTypeface);
        imageView.setBackgroundResource(R.anim.loading);
        AnimationDrawable drawable = (AnimationDrawable) imageView.getBackground() ;
        drawable.start() ;
        return dialog ;
    }


}
