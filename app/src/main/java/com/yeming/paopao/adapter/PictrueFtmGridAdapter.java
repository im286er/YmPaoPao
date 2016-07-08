package com.yeming.paopao.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;
import com.yeming.paopao.R;
import com.yeming.paopao.bean.Pictrue;
import com.yeming.paopao.utils.ImageLoadOptions;
import com.yeming.paopao.views.ToastView;

import java.util.List;

import cn.bmob.v3.datatype.BmobFile;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-02-03 21:12
 * version: V1.0
 * Description: 图片瀑布流适配器
 */
public class PictrueFtmGridAdapter extends BaseAdapter {

    private static String[] colorArray = new String[] { "#FF467283",
            "#FF997283", "#FF3BBD79", "#FF1493" };
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private Context context;
    private LayoutInflater mInflater;
    private List<Pictrue> list ;

    public PictrueFtmGridAdapter(Context context,ImageLoader imageLoader,List<Pictrue> list){
        this.context = context ;
        this.list = list ;
        this.mInflater = LayoutInflater.from(context);
        this.imageLoader = imageLoader ;
        this.options = ImageLoadOptions.getOptionsForNormal() ;
    }

    public void setList(List<Pictrue> list){
        this.list = list ;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ModelHodler holder = null;
        Pictrue pictrue = list.get(i) ;
        if(view == null){
          holder = new ModelHodler() ;
            view = mInflater.inflate(R.layout.pictrue_grid_item_layout,null) ;
            holder.imageView = (ImageView) view.findViewById(R.id.grid_item_image) ;
            holder.textView = (TextView) view.findViewById(R.id.grid_item_text);
            view.setTag(holder);
        }else{
            holder = (ModelHodler) view.getTag();
        }
        BmobFile file = pictrue.getPicture() ;
        imageLoader.displayImage(file.getFileUrl(context),holder.imageView,options);
    /*    Picasso.with(context)
                .load(file.getFileUrl(context))
                .placeholder(R.drawable.default_load_bg)
                .error(R.drawable.default_fail_bg)
                .into(holder.imageView);*/
        //holder.imageView.setBackgroundResource(R.drawable.default_load_bg);
        holder.textView.setText(pictrue.getImageTips());
        holder.textView.setTextColor(Color.parseColor(colorArray[i
                % colorArray.length]));
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastView.showToast(context,"on click!", Toast.LENGTH_SHORT);
            }
        });

        return view;
    }

    private final class ModelHodler {
        public ImageView imageView;
        public TextView textView;
    }
}
