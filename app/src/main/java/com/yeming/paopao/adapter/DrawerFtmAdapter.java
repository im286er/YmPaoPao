package com.yeming.paopao.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yeming.paopao.R;
import com.yeming.paopao.app.YmApplication;
import com.yeming.paopao.bean.DrawerItemModel;

import java.util.List;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-30 21:24
 * version: V1.0
 * Description:  导航页list适配器
 */
public class DrawerFtmAdapter extends BaseAdapter {

    private Activity activity;
    private List<DrawerItemModel> listData;
    private LayoutInflater mInflater;
    private Typeface englishtypeface, chineseTypeface;

    public DrawerFtmAdapter(Activity activity,List<DrawerItemModel> listData){
        this.activity = activity;
        this.listData = listData ;
        this.mInflater = LayoutInflater.from(activity);
        englishtypeface = YmApplication.englishTypeface;
        chineseTypeface = YmApplication.chineseTypeface;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = mInflater
                    .inflate(R.layout.drawer_fmt_list_item, viewGroup, false);
            holder = new ViewHolder();
            holder.leftImage = (ImageView) view.findViewById(R.id.item_left_image);
            holder.centerText = (TextView) view.findViewById(R.id.item_center_text);
            holder.numberImage = (ImageView) view
                    .findViewById(R.id.item_right_image);
            holder.number = (TextView) view
                    .findViewById(R.id.item_right_image_text);

            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }
        DrawerItemModel itemModel = listData.get(i) ;
        holder.leftImage.setBackgroundResource(itemModel.getLeftDrawbleResource());
        holder.centerText.setText(itemModel.getCenterTitle());
        holder.centerText.setTypeface(chineseTypeface);
        if (itemModel.getNumber() != null && !itemModel.getNumber().equals("")) {
            int numb = Integer.parseInt(itemModel.getNumber());
            holder.number.setText(itemModel.getNumber());
           // holder.numberImage.setBackgroundResource(R.drawable.numb_bg_2);
        }else{
            holder.number.setText("");
        }
        return view;
    }

    class ViewHolder {
        public ImageView leftImage;
        public TextView centerText;
        public ImageView numberImage;
        public TextView number;

    }
}
