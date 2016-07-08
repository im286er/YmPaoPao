package com.yeming.paopao.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.yeming.paopao.R;
import com.yeming.paopao.app.YmApplication;
import com.yeming.paopao.aty.EditUserInfoActivity;
import com.yeming.paopao.aty.EditUserSignActivity;
import com.yeming.paopao.aty.OtherUserInfoActivity;
import com.yeming.paopao.aty.ZoomImageActivity;
import com.yeming.paopao.bean.Paopao;
import com.yeming.paopao.bean.User;
import com.yeming.paopao.utils.BitmapUtil;
import com.yeming.paopao.utils.ImageLoadOptions;
import com.yeming.paopao.utils.LogUtil;
import com.yeming.paopao.utils.TimeUtil;
import com.yeming.paopao.views.third.CircleImageView;

import java.util.List;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-02-07 19:01
 * version: V1.0
 * Description:  泡泡列表适配器
 */
public class PaopaoFtmListAdapter extends BaseAdapter{

    private String TAG = "PaopaoFtmListAdapter" ;
    private Context context;
    private LayoutInflater mInflater;
    private List<Paopao> list ;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public PaopaoFtmListAdapter(Context context,List<Paopao> list,ImageLoader imageLoader){
        this.context = context ;
        mInflater = LayoutInflater.from(context) ;
        this.list = list ;
        this.imageLoader = imageLoader ;
        this.options = ImageLoadOptions.getOptionsById(R.drawable.user_icon_default_main) ;
    }

    public PaopaoFtmListAdapter(Context context){
        this.context = context ;
        mInflater = LayoutInflater.from(context) ;
    }

    public void setList(List<Paopao> list){
        this.list = list ;
    }

    @Override
    public int getCount() {
       //return 20 ;
       return list.size() == 0 ? 0:list.size();
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

        //view = mInflater.inflate(R.layout.paopao_list_item_layout,null) ;

        final ViewHolder viewHolder;
        if(view == null){
            viewHolder = new ViewHolder();
            view = mInflater.inflate(R.layout.paopao_list_item_layout, null);
            viewHolder.userName = (TextView)view.findViewById(R.id.item_user_name);
            viewHolder.userIcon = (CircleImageView)view.findViewById(R.id.item_user_icon);
            viewHolder.contentText = (TextView)view.findViewById(R.id.item_content_text);
            viewHolder.contentImage = (ImageView)view.findViewById(R.id.item_content_image);
            viewHolder.like = (TextView)view.findViewById(R.id.item_like_num);
            viewHolder.share = (TextView)view.findViewById(R.id.item_share_num);
            viewHolder.time = (TextView)view.findViewById(R.id.item_content_time);
            viewHolder.device = (TextView)view.findViewById(R.id.item_content_device);
            viewHolder.comment = (TextView)view.findViewById(R.id.item_comment_num);
            viewHolder.action_comment = (LinearLayout) view.findViewById(R.id.item_action_comment);
            viewHolder.action_like = (LinearLayout) view.findViewById(R.id.item_action_like);
            viewHolder.action_share = (LinearLayout) view.findViewById(R.id.item_action_share);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)view.getTag();
        }
        final Paopao paopao = list.get(i) ;
        //LogUtil.d(TAG,"-----paopao--"+paopao.toString());
        final User user = paopao.getUser() ;
        String avatarUrl = null;
        if(user == null){
            LogUtil.d(TAG, i + "==USER IS NULL");
        }
       /* if(user.getAvatarUrl()==null){
            LogUtil.i(TAG,i+"==USER avatar IS NULL");
        }
        if(user.getAvatar()!=null){
            avatarUrl = user.getAvatar().getFileUrl(context) ;
        }*/
        avatarUrl = user.getAvatarUrl() ;
        imageLoader.displayImage(avatarUrl,viewHolder.userIcon,options);
        viewHolder.userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String objId = user.getObjectId() ;
                if(YmApplication.getCurrentUser().getObjectId().equals(objId)){
                    Intent intent = new Intent() ;
                    intent.setClass(context, EditUserInfoActivity.class) ;
                    context.startActivity(intent);
                }else {
                    //  点击跳转到用户
                    Intent intent = new Intent();
                    intent.putExtra("user", user);
                    intent.setClass(context, OtherUserInfoActivity.class);
                    context.startActivity(intent);
                }
            }
        });

        viewHolder.userName.setText(paopao.getUser().getNickname());
        viewHolder.contentText.setText(paopao.getContent());

        String device = paopao.getDevice();
        if(!device.isEmpty()){
            final String format = "来自 %s";
            device = String.format(format, device);
        }

        String timeMillis = paopao.getCreateTimeMillis() ;
        if(null != timeMillis){
            viewHolder.time.setVisibility(View.VISIBLE);
            viewHolder.time.setText(TimeUtil.dayToNow(Long.parseLong(timeMillis)));
        }else{
            viewHolder.time.setVisibility(View.GONE);
            viewHolder.time.setText("");
        }

        viewHolder.device.setText(device);

        // 设置字体风格
        viewHolder.userName.setTypeface(YmApplication.chineseTypeface);
        viewHolder.contentText.setTypeface(YmApplication.chineseTypeface);
        viewHolder.time.setTypeface(YmApplication.chineseTypeface);
        viewHolder.device.setTypeface(YmApplication.chineseTypeface);
        viewHolder.comment.setTypeface(YmApplication.chineseTypeface);
        viewHolder.like.setTypeface(YmApplication.chineseTypeface);
        viewHolder.share.setTypeface(YmApplication.chineseTypeface);

        if(null == paopao.getImageUrl()){
            viewHolder.contentImage.setVisibility(View.GONE);
        }else{
            viewHolder.contentImage.setVisibility(View.VISIBLE);
            imageLoader.displayImage(paopao.getImageUrl() == null ? "" : paopao.getImageUrl(), viewHolder.contentImage,
                    ImageLoadOptions.getOptionsById(R.drawable.default_load_bg),
                    new SimpleImageLoadingListener() {

                        @Override
                        public void onLoadingComplete(String imageUri, View view,
                                                      Bitmap loadedImage) {
                            // TODO Auto-generated method stub
                            super.onLoadingComplete(imageUri, view, loadedImage);
                            //  设置图片显示宽高  4.0f
                            float[] cons = BitmapUtil.getBitmapConfiguration(loadedImage, viewHolder.contentImage, 4.0f);
                            RelativeLayout.LayoutParams layoutParams =
                                    new RelativeLayout.LayoutParams((int) cons[0], (int) cons[1]);
                            layoutParams.addRule(RelativeLayout.BELOW, R.id.item_content_text);
                            viewHolder.contentImage.setLayoutParams(layoutParams);
                        }

                    });
            viewHolder.contentImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 跳转图片显示
                    Intent intent = new Intent();
                    intent.putExtra("url",  paopao.getImageUrl());
                    intent.setClass(context, ZoomImageActivity.class);
                    context.startActivity(intent);
                }
            });
        }
        return view;
    }


    public static class ViewHolder{
        public CircleImageView userIcon;
        public TextView userName;
        public TextView contentText;
        public ImageView contentImage;

        public TextView like;
        public TextView share;
        public TextView comment;
        public TextView time ;
        public TextView device ;

        public LinearLayout action_comment ;
        public LinearLayout action_share ;
        public LinearLayout action_like ;
    }
}
