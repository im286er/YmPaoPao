package com.yeming.paopao.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yeming.paopao.R;
import com.yeming.paopao.app.YmApplication;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-31 16:10
 * version: V1.0
 * Description: 退出应用选择view
 */
public class ExitActionView {

    /**
     * @param context
     * @param actionSheetSelected
     * @param cancelListener
     * @return
     * 弹出退出选择区域
     */
    public static Dialog showExitActionSheet(Context context,
                                 final OnActionSheetSelected actionSheetSelected,
                                 DialogInterface.OnCancelListener cancelListener) {
        final Dialog dialog = new Dialog(context, R.style.exitActionSheet);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.exit_action_layout, null);
        final int cFullFillWidth = 10000;
        layout.setMinimumWidth(cFullFillWidth);

        Typeface chineseTypeface = YmApplication.chineseTypeface;
        TextView mExit = (TextView) layout.findViewById(R.id.exit);
        TextView mCancel = (TextView) layout.findViewById(R.id.cancle);

        TextView textView = (TextView) layout.findViewById(R.id.textView);
        mExit.setTypeface(chineseTypeface);
        mCancel.setTypeface(chineseTypeface);
        textView.setTypeface(chineseTypeface);
        mExit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                actionSheetSelected.onClick(0);
                dialog.dismiss();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                actionSheetSelected.onClick(1);
                dialog.dismiss();
            }
        });

        Window w = dialog.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        dialog.onWindowAttributesChanged(lp);
        dialog.setCanceledOnTouchOutside(false);
        if (cancelListener != null)
            dialog.setOnCancelListener(cancelListener);

        dialog.setContentView(layout);
        dialog.show();

        return dialog;
    }

    public interface OnActionSheetSelected {
        void onClick(int whichButton);
    }
}
