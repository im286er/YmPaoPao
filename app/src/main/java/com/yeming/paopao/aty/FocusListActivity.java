package com.yeming.paopao.aty;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yeming.paopao.R;
import com.yeming.paopao.adapter.FansListAdapter;
import com.yeming.paopao.app.YmApplication;
import com.yeming.paopao.bean.User;
import com.yeming.paopao.commons.Constant;
import com.yeming.paopao.utils.LogUtil;
import com.yeming.paopao.views.MySwipeRefreshLayout;
import com.yeming.paopao.views.ToastView;
import com.yeming.paopao.views.third.DialogView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-28 21:27
 * version: V1.0
 * Description:   关注列表
 */
public class FocusListActivity extends Activity {

    private static final String TAG = "FansListActivity";
    private Context context ;
    private MySwipeRefreshLayout swipeRefreshLayout ;
    private ListView listView ;
    private FansListAdapter adapter ;
    private ImageLoader imageLoader = ImageLoader.getInstance();   //  图片加载器
    private List<User> mList ;
    private int focussPageNum = 0 ;    //  关注列表当前页
    public enum RefreshType{   //  加载数据操作类型  刷新 加载更多
        REFRESH,LOAD_MORE
    }
    private RefreshType mRefreshType = RefreshType.LOAD_MORE;
    private User user ;
    private Dialog loadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //    this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        super.setContentView(R.layout.fans_list_layout);

        YmApplication.getInstance().addActivity(this);
        context = this ;

        setActionBar();

        Intent intent = getIntent() ;
        user = (User) intent.getSerializableExtra("user");
        mList = new ArrayList<User>() ;
        loadDialog = DialogView.loadDialog(context, R.string.loading) ;

        initView();
        loadDialog.show();
        getUserFocusList();

        initListener();

        adapter = new FansListAdapter(context,mList,imageLoader) ;
        AnimationAdapter animAdapter = new ScaleInAnimationAdapter(adapter);
        animAdapter.setAbsListView(listView);
        animAdapter.setInitialDelayMillis(300);
        listView.setAdapter(animAdapter);



    }

    /**
     *
     */
    private void initView(){
        swipeRefreshLayout = (MySwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.green, R.color.green, R.color.green, R.color.green);
        listView = (ListView) findViewById(R.id.listView);
    }

    private void initListener(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                focussPageNum = 0 ;
                mRefreshType = RefreshType.REFRESH;
                swipeRefreshLayout.setRefreshing(true);
                getUserFocusList();

            }
        });

        swipeRefreshLayout.setOnLoadListener(new MySwipeRefreshLayout.OnLoadListener() {

            @Override
            public void onLoadMore() {
                LogUtil.d("FansListActivity", "---------onLoadMore---------");
                ToastView.showToast(context, "正在加载。。。", Toast.LENGTH_SHORT);
                mRefreshType = RefreshType.LOAD_MORE;
                swipeRefreshLayout.setLoading(true);
                getUserFocusList();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent() ;
                User user = mList.get(i) ;
                if(YmApplication.getCurrentUser().getObjectId().equals(user.getObjectId())){
                    intent.setClass(context, EditUserInfoActivity.class);
                    startActivity(intent);
                }else {
                    intent.putExtra("user", user);
                    intent.setClass(context, OtherUserInfoActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 获取用户关注列表
     */
    public void getUserFocusList(){
        BmobQuery<User> query = new BmobQuery<User>() ;
        query.order("-createdAt") ;
        query.setLimit(Constant.PAGE_SIZE);
        query.setSkip(Constant.PAGE_SIZE*(focussPageNum++));
        query.addWhereRelatedTo("focus", new BmobPointer(user));
        query.findObjects(context, new FindListener<User>() {

            @Override
            public void onSuccess(List<User> users) {
                if(loadDialog.isShowing()){
                    LogUtil.d(TAG, "-----getUserFansList loadDialog dismiss-----");
                    loadDialog.dismiss();
                }
                LogUtil.d(TAG,"---getUserFocusList Success----"+users.size());
                if(users.size()!=0&&users.get(users.size()-1)!=null){
                    if(mRefreshType==RefreshType.REFRESH){     //刷新
                        mList.clear();
                        swipeRefreshLayout.setRefreshing(false);// 设置状态
                    }else if(mRefreshType==RefreshType.LOAD_MORE){ //  加载更多
                        swipeRefreshLayout.setLoading(false);// 设置状态
                    }
                    if(users.size()<Constant.PAGE_SIZE){
                        ToastView.showToast(context,"~已加载完所有数据~",Toast.LENGTH_SHORT);
                    }
                    mList.addAll(users) ;
                    adapter.setList(mList);
                    adapter.notifyDataSetChanged();
                }else{
                    LogUtil.d(TAG,"---getUserFocusList Success----暂无更多数据");
                    ToastView.showToast(context,"~暂无更多数据~",Toast.LENGTH_SHORT);
                    focussPageNum--;
                    // 设置状态
                    if(mRefreshType==RefreshType.REFRESH){
                        swipeRefreshLayout.setRefreshing(false);
                    }else if(mRefreshType==RefreshType.LOAD_MORE){
                        swipeRefreshLayout.setLoading(false);
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                if(loadDialog.isShowing()){
                    LogUtil.d(TAG, "-----getUserFansList loadDialog dismiss-----");
                    loadDialog.dismiss();
                }
                LogUtil.d(TAG,"-----getUserFocusList Error--code="+i+"____"+s);
                if(focussPageNum > 0){
                    focussPageNum--;
                }
                if(mRefreshType==RefreshType.REFRESH){
                    LogUtil.d(TAG,"-----getUserFocusList Error--RefreshType.REFRESH");
                    swipeRefreshLayout.setRefreshing(false);
                }else if(mRefreshType==RefreshType.LOAD_MORE){
                    LogUtil.d(TAG, "-----getUserFocusList Error--RefreshType.LOAD_MORE");
                    swipeRefreshLayout.setLoading(false);
                }
            }
        }) ;
    }

    /**
     * actionbar style
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void setActionBar(){
        //this.getActionBar().setTitle("PaoPao");
        getActionBar().setBackgroundDrawable(
                this.getBaseContext().getResources()
                        .getDrawable(R.drawable.actionbar_bg));
        //getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        //getActionBar().setTitle(user.getNickname());
        int titleId = Resources.getSystem().getIdentifier("action_bar_title",
                "id", "android");
        TextView textView = (TextView) findViewById(titleId);
        textView.setTypeface(YmApplication.chineseTypeface);
        textView.setTextColor(0xFFdfdfdf);
        textView.setTextSize(20);
        textView.setGravity(Gravity.CENTER);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId() ;
        switch (id){
            case android.R.id.home:
                onBackPressed();
                break ;
        }
        return super.onMenuItemSelected(featureId, item);
    }
}
