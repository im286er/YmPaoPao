package com.yeming.paopao.aty;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.yeming.paopao.R;
import com.yeming.paopao.adapter.CommentLisrAdapter;
import com.yeming.paopao.app.YmApplication;
import com.yeming.paopao.bean.Comment;
import com.yeming.paopao.bean.Paopao;
import com.yeming.paopao.bean.User;
import com.yeming.paopao.commons.Constant;
import com.yeming.paopao.utils.BitmapUtil;
import com.yeming.paopao.utils.ImageLoadOptions;
import com.yeming.paopao.utils.LogUtil;
import com.yeming.paopao.views.MyListView;
import com.yeming.paopao.views.ToastView;
import com.yeming.paopao.views.third.CircleImageView;
import com.yeming.paopao.views.third.DialogView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-02-11 21:16
 * version: V1.0
 * Description:
 */
public class PaopaoDetailActivity extends Activity{

    private String TAG = "PaopaoDetailActivity" ;
    private ScrollView detail_scroll ;
    private Context context ;
    private LinearLayout area_commit ,item_action_comment,action_share,action_comment,action_like;
    private ListView commentList ;
   // private MyListView commentList ;
    private List<Comment> list ;
    private TextView noCommtentTip ;
    boolean flag = false ;
    private Paopao paopao ;
    private CircleImageView circleImageView ;
    private TextView userName,time,content,device,like,share,comment ;
    private EditText comment_content ;
    private Button comment_commit ;
    private ImageView contentImg ;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private CommentLisrAdapter commentLisrAdapter ;
    private boolean areaCommitIsShow = false ;  // 评论框显示状态
    private int pageNum = 0 ;
    private Dialog loadDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.paopaodetail_layout);

        YmApplication.getInstance().addActivity(this);
        context = this ;
        setActionBar();
        loadDialog = DialogView.loadDialog(context,R.string.pushing) ;
        list = new ArrayList<Comment>() ;
        paopao = (Paopao) getIntent().getSerializableExtra("paopao_detail");
        initView();

    //    commentLisrAdapter = new CommentLisrAdapter(context) ;
        commentLisrAdapter = new CommentLisrAdapter(context,list,imageLoader) ;

        setDetailData();
        initCommentList();
        getComment();

    }

    /**
     * 控件
     */
    private void initView(){

        detail_scroll = (ScrollView) findViewById(R.id.detail_scroll);

        circleImageView = (CircleImageView) findViewById(R.id.item_user_icon);
        userName = (TextView) findViewById(R.id.item_user_name);
        time = (TextView) findViewById(R.id.item_content_time) ;
        content = (TextView) findViewById(R.id.item_content_text) ;
        contentImg = (ImageView) findViewById(R.id.item_content_image) ;
        device = (TextView) findViewById(R.id.item_content_device) ;

        like = (TextView)findViewById(R.id.item_like_num);
        share = (TextView)findViewById(R.id.item_share_num);
        comment = (TextView)findViewById(R.id.item_comment_num);
        action_comment = (LinearLayout) findViewById(R.id.item_action_comment);
        action_like = (LinearLayout) findViewById(R.id.item_action_like);
        action_share = (LinearLayout) findViewById(R.id.item_action_share);

        noCommtentTip = (TextView) findViewById(R.id.noCommtentTip);
        commentList = (ListView) findViewById(R.id.comment_list);
        noCommtentTip.setTypeface(YmApplication.chineseTypeface);

        comment_content = (EditText) findViewById(R.id.comment_content);
        area_commit = (LinearLayout) findViewById(R.id.area_commit);
        comment_commit = (Button) findViewById(R.id.comment_commit);

        item_action_comment = (LinearLayout) findViewById(R.id.item_action_comment);

        userName.setTypeface(YmApplication.chineseTypeface);
        time.setTypeface(YmApplication.chineseTypeface);
        device.setTypeface(YmApplication.chineseTypeface);
        content.setTypeface(YmApplication.chineseTypeface);
        like.setTypeface(YmApplication.chineseTypeface);
        share.setTypeface(YmApplication.chineseTypeface);
        comment.setTypeface(YmApplication.chineseTypeface);
        comment_content.setTypeface(YmApplication.chineseTypeface);
        comment_commit.setTypeface(YmApplication.chineseTypeface);


        // 评论框显示 隐藏
        item_action_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        noCommtentTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getComment();
            }
        });

        //  点击头像
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String objId = paopao.getUser().getObjectId() ;
                if(YmApplication.getCurrentUser().getObjectId().equals(objId)){
                    Intent intent = new Intent() ;
                    intent.setClass(context, EditUserInfoActivity.class) ;
                    context.startActivity(intent);
                }else {
                    //  点击跳转到用户
                    Intent intent = new Intent();
                    intent.putExtra("user", paopao.getUser());
                    intent.setClass(context, OtherUserInfoActivity.class);
                    context.startActivity(intent);
                }
            }
        });

        //  提交评论
        comment_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String commentStr = comment_content.getText().toString().trim();
                if("".equals(commentStr)){
                    ToastView.showToast(context,"请输入评论内容!", Toast.LENGTH_SHORT);
                    return ;
                }
                if(areaCommitIsShow){   //  隐藏评论输入框
                    hideAreaCommit();
                }
                loadDialog.show();
                User user = BmobUser.getCurrentUser(context,User.class) ;
                pushComment(user,commentStr);
            }
        });

        contentImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转图片显示
                Intent intent = new Intent();
                intent.putExtra("url", paopao.getImageUrl());
                intent.setClass(context, ZoomImageActivity.class);
                context.startActivity(intent);
            }
        });
    }

    /**
     * 初始化评论列表
     */
    private void initCommentList(){
        noCommtentTip.setVisibility(View.GONE);
        commentList.setVisibility(View.VISIBLE);
        commentList.setAdapter(commentLisrAdapter);
        setListViewHeightBasedOnChildren(commentList);
    //    detail_scroll.smoothScrollTo(0, 0);
    }

    /**
     * 填充数据
     */
    private void setDetailData(){
        User user = paopao.getUser() ;
        String avatarUrl = null;
        /*if(user.getAvatar()!=null){
            avatarUrl = user.getAvatar().getFileUrl(this) ;
        }*/
        avatarUrl = user.getAvatarUrl() ;
        imageLoader.displayImage(avatarUrl,circleImageView, ImageLoadOptions.getOptionsById(R.drawable.user_icon_default_main));

        String deviceStr = paopao.getDevice();
        if(!deviceStr.isEmpty()){
            final String format = "来自 %s";
            deviceStr = String.format(format, deviceStr);
        }

        String timeAt = paopao.getCreatedAt() ;

        userName.setText(paopao.getUser().getNickname());
        content.setText(paopao.getContent());
        device.setText(deviceStr);
        time.setText(timeAt);

        if(null == paopao.getImageUrl()){
            contentImg.setVisibility(View.GONE);
        }else {
            contentImg.setVisibility(View.VISIBLE);
            imageLoader.displayImage(paopao.getImageUrl() == null ? "" : paopao.getImageUrl(), contentImg,
                    ImageLoadOptions.getOptionsById(R.drawable.default_load_bg),
                    new SimpleImageLoadingListener() {

                        @Override
                        public void onLoadingComplete(String imageUri, View view,
                                                      Bitmap loadedImage) {
                            // TODO Auto-generated method stub
                            super.onLoadingComplete(imageUri, view, loadedImage);
                            //  设置图片显示宽高  4.0f
                            float[] cons = BitmapUtil.getBitmapConfiguration(loadedImage, contentImg, 4.0f);
                            RelativeLayout.LayoutParams layoutParams =
                                    new RelativeLayout.LayoutParams((int) cons[0], (int) cons[1]);
                            layoutParams.addRule(RelativeLayout.BELOW, R.id.item_content_text);
                            contentImg.setLayoutParams(layoutParams);
                        }

                    });
        }
    }

    /**
     * 获取评论列表
     */
    private void getComment(){
        BmobQuery<Comment> query = new BmobQuery<Comment>();
        query.addWhereRelatedTo("comment", new BmobPointer(paopao));
        query.include("user");
        query.order("-createdAt");
        query.setLimit(Constant.COMMENT_PAGE_SIZE);
        query.setSkip(Constant.COMMENT_PAGE_SIZE*(pageNum++));
        query.findObjects(this, new FindListener<Comment>() {

            @Override
            public void onSuccess(List<Comment> data) {
                LogUtil.i(TAG, "get comment success!" + data.size());
                if(data.size()!=0 && data.get(data.size()-1)!=null){

                    noCommtentTip.setText("更多评论");
                    noCommtentTip.setVisibility(View.VISIBLE);
                    commentLisrAdapter.getList().addAll(data);
                    commentLisrAdapter.notifyDataSetChanged();
                    setListViewHeightBasedOnChildren(commentList);
                    if(data.size()<Constant.COMMENT_PAGE_SIZE){
                        ToastView.showToast(context,"已加载完所有评论~", Toast.LENGTH_SHORT);
                        noCommtentTip.setText("暂无更多评论~");
                        noCommtentTip.setVisibility(View.VISIBLE);
                    }

                }else{
                    ToastView.showToast(context,"暂无更多评论~", Toast.LENGTH_SHORT);
                    noCommtentTip.setText("暂无更多评论~");
                    noCommtentTip.setVisibility(View.VISIBLE);
                    pageNum--;
                }
            }

            @Override
            public void onError(int arg0, String arg1) {
                ToastView.showToast(context, "获取评论失败。请检查网络~", Toast.LENGTH_SHORT);
                noCommtentTip.setVisibility(View.VISIBLE);
                pageNum--;
            }
        });
    }


    /**
     * @param user   用户
     * @param content  评论内容
     * @Description 评论
     */
    private void pushComment(User user,String content){
        final Comment comment = new Comment() ;
        comment.setUser(user);
        comment.setPaopao(paopao);
        comment.setContent(content);
        comment.save(context,new SaveListener() {
            @Override
            public void onSuccess() {
                ToastView.showToast(context,"评论成功。", Toast.LENGTH_SHORT);
            //    if(commentLisrAdapter.getList().size()<Constant.PAGE_SIZE){
                    commentLisrAdapter.getList().add(0,comment);
                    commentLisrAdapter.notifyDataSetChanged();
                    setListViewHeightBasedOnChildren(commentList);
            //    }
                comment_content.setText("");
                hideSoftInput();
                //将该评论与强语绑定到一起
                BmobRelation relation = new BmobRelation();
                relation.add(comment);
                paopao.setComment(relation);
                paopao.update(context, new UpdateListener() {

                    @Override
                    public void onSuccess() {
                        LogUtil.i(TAG, "更新评论成功。");
                        loadDialog.dismiss();
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        LogUtil.i(TAG, "更新评论失败。"+arg1);
                        loadDialog.dismiss();
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
                ToastView.showToast(context,"评论失败。请检查网络~", Toast.LENGTH_SHORT);
                loadDialog.dismiss();
            }
        });

    }

    /**
     * 评论框显示动画
     */
    private void showAreaCommit(){
        Animation animation = AnimationUtils.loadAnimation(context,R.anim.in) ;
        area_commit.setAnimation(animation);
        area_commit.setVisibility(View.VISIBLE);
        animation.start();
        areaCommitIsShow = true ;
    }
    /**
     * 评论框隐藏动画
     */
    private void hideAreaCommit(){
        Animation animation = AnimationUtils.loadAnimation(context,R.anim.out) ;
        area_commit.setAnimation(animation);
        area_commit.setVisibility(View.GONE);
        animation.start();
        areaCommitIsShow = false ;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void setActionBar(){
        //this.getActionBar().setTitle("PaoPao");
        getActionBar().setBackgroundDrawable(
                this.getBaseContext().getResources()
                        .getDrawable(R.drawable.actionbar_bg));
        //getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayUseLogoEnabled(false);
        getActionBar().setDisplayShowHomeEnabled(false);
        int titleId = Resources.getSystem().getIdentifier("action_bar_title",
                "id", "android");
        TextView textView = (TextView) findViewById(titleId);
        textView.setTypeface(YmApplication.chineseTypeface);
        textView.setTextColor(0xFFdfdfdf);
        textView.setTextSize(20);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.comment_edit_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId() ;
        switch (id){
            case R.id.action_edit:
                if(!areaCommitIsShow){
                    showAreaCommit();
                }else{
                    hideAreaCommit();
                //    detail_scroll.smoothScrollTo(0, 0);
                }
                break ;
            case android.R.id.home:
                onBackPressed();
                break ;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * @param listView
     * 动态设置listview的高度
     * item 总布局必须是linearLayout
     */
    private void setListViewHeightBasedOnChildren(ListView listView){
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount()-1))
                +15;
        listView.setLayoutParams(params);

    }

    /**
     * 隐藏键盘
     */
    private void hideSoftInput(){
        InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(comment_content.getWindowToken(), 0);
    }

}
