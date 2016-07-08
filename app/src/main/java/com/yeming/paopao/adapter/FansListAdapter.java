package com.yeming.paopao.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yeming.paopao.R;
import com.yeming.paopao.app.YmApplication;
import com.yeming.paopao.bean.User;
import com.yeming.paopao.utils.ImageLoadOptions;
import com.yeming.paopao.views.third.CircleImageView;

import java.util.List;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-28 21:27
 * version: V1.0
 * Description:
 */
public class FansListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater mInflater;
    private List<User> list ;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public FansListAdapter(Context context,List<User> list,ImageLoader imageLoader){
        this.context = context ;
        mInflater = LayoutInflater.from(context) ;
        this.list = list ;
        this.imageLoader = imageLoader ;
        this.options = ImageLoadOptions.getOptionsById(R.drawable.user_icon_default_main) ;
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

        final ViewHolder viewHolder;
        if(view == null){
            viewHolder = new ViewHolder();
            view = mInflater.inflate(R.layout.fans_list_item_layout,null) ;
            viewHolder.userName = (TextView)view.findViewById(R.id.fans_item_user_name);
         //   viewHolder.userIcon = (CircleImageView)view.findViewById(R.id.fans_item_user_icon);
            viewHolder.userIcon = (ImageView)view.findViewById(R.id.fans_item_user_icon);
            viewHolder.sign = (TextView)view.findViewById(R.id.fans_item_sign_text);
           view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)view.getTag();
        }

        User user = list.get(i) ;
        viewHolder.userName.setTypeface(YmApplication.chineseTypeface);
        viewHolder.sign.setTypeface(YmApplication.chineseTypeface);

        String avatarUrl = user.getAvatarUrl() ;
        imageLoader.displayImage(avatarUrl,viewHolder.userIcon,options);
        viewHolder.userName.setText(user.getNickname());
        viewHolder.sign.setText(user.getSign());

        return view;
    }

    public List<User> getList() {
        return list;
    }

    public void setList(List<User> list) {
        this.list = list;
    }

    public static class ViewHolder{
      //  public CircleImageView userIcon;
        public ImageView userIcon;
        public TextView userName;
        public TextView sign;
    }
}
