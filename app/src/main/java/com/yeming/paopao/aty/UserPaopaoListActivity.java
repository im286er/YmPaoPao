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
import com.yeming.paopao.adapter.PaopaoFtmListAdapter;
import com.yeming.paopao.app.YmApplication;
import com.yeming.paopao.bean.Paopao;
import com.yeming.paopao.bean.User;
import com.yeming.paopao.commons.Constant;
import com.yeming.paopao.commons.DataInfoCache;
import com.yeming.paopao.utils.LogUtil;
import com.yeming.paopao.views.MySwipeRefreshLayout;
import com.yeming.paopao.views.ToastView;
import com.yeming.paopao.views.third.DialogView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-28 21:27
 * version: V1.0
 * Description:  用户泡泡列表
 */
public class UserPaopaoListActivity extends Activity{

    private String TAG = "PaoPaoFragment" ;
    private Context context ;
    private MySwipeRefreshLayout swipeRefreshLayout ;
    private ListView listView ;
    private PaopaoFtmListAdapter adapter ;
    private ImageLoader imageLoader = ImageLoader.getInstance();   //  图片加载器
    private List<Paopao> mList ;
    private int pageNum = 0 ;    //  当前页
    public enum RefreshType{   //  加载数据操作类型  刷新 加载更多
        REFRESH,LOAD_MORE
    }
    private RefreshType mRefreshType = RefreshType.LOAD_MORE;
    private boolean noMore = false ;  // 判断是否还有数据,为true时不执行上拉加载更多
    private static final int REQUEST_EDIT_MAOPAO = 1 ;  //  跳转编辑泡泡页面请求码
    private Dialog loadDialog;
    private User user ;

    private boolean isCache = true ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.paopao_fmt_layout);

        YmApplication.getInstance().addActivity(this);
        context = this ;

        setActionBar();

        loadDialog = DialogView.loadDialog(context, R.string.loading) ;
        setActionBar();

        Intent intent = getIntent() ;
        user = (User) intent.getSerializableExtra("user");
        mList = new ArrayList<Paopao>() ;

        initView();

        loadDialog.show();
        getUserPaopaoList() ;

        initListener();

        adapter = new PaopaoFtmListAdapter(context,mList,imageLoader) ;
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
                pageNum = 0 ;
                mRefreshType = RefreshType.REFRESH;
                swipeRefreshLayout.setRefreshing(true);
                getUserPaopaoList();

            }
        });

        swipeRefreshLayout.setOnLoadListener(new MySwipeRefreshLayout.OnLoadListener() {

            @Override
            public void onLoadMore() {
                LogUtil.d("FansListActivity", "---------onLoadMore---------");
                ToastView.showToast(context, "正在加载。。。", Toast.LENGTH_SHORT);
                mRefreshType = RefreshType.LOAD_MORE;
                swipeRefreshLayout.setLoading(true);
                getUserPaopaoList() ;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent() ;
                Paopao paopao = mList.get(i) ;
                intent.putExtra("paopao_detail",paopao) ;
                intent.setClass(context, PaopaoDetailActivity.class) ;
                startActivity(intent);
            }
        });
    }


    /**
     * 获取用户泡泡列表
     */
    private void getUserPaopaoList(){
        BmobQuery<Paopao> query = new BmobQuery<Paopao>() ;
        query.order("-createdAt") ;
        query.setLimit(Constant.PAGE_SIZE);
        BmobDate date = new BmobDate(new Date(System.currentTimeMillis()));
        query.addWhereLessThan("createdAt", date);
        query.setSkip(Constant.PAGE_SIZE*(pageNum++));
        query.include("user");
        query.addWhereRelatedTo("paopao", new BmobPointer(user));
        query.findObjects(context,new FindListener<Paopao>() {
            @Override
            public void onSuccess(List<Paopao> paopaos) {
                if(loadDialog.isShowing()){
                    LogUtil.d(TAG, "-----getUserPaopaoList loadDialog dismiss 2-----");
                    loadDialog.dismiss();
                }
                LogUtil.d(TAG,"---getUserPaopaoList Success----"+paopaos.size());
                if(paopaos.size()!=0&&paopaos.get(paopaos.size()-1)!=null){
                    if(mRefreshType==RefreshType.REFRESH){     //刷新
                        mList.clear();
                        swipeRefreshLayout.setRefreshing(false);// 设置状态
                        //  每次刷新都缓存第一页数据
                    //    DataInfoCache.saveListPaopaos(context, (ArrayList<Paopao>) paopaos);
                    }else if(mRefreshType==RefreshType.LOAD_MORE){ //  加载更多
                        swipeRefreshLayout.setLoading(false);// 设置状态
                    }
                    if(paopaos.size()<Constant.PAGE_SIZE){
                        ToastView.showToast(context,"~已加载完所有数据~",Toast.LENGTH_SHORT);
                    }
                    mList.addAll(paopaos) ;
                    adapter.setList(mList);
                    adapter.notifyDataSetChanged();
                   /* if(pageNum == 1){  //  加载完第一页数据后则缓存，不管是刷新还是加载操作
                        DataInfoCache.saveListPaopaos(context, (ArrayList<Paopao>) mList);
                    }*/
                }else{
                    LogUtil.d(TAG,"---getUserPaopaoList Success----暂无更多数据");
                    ToastView.showToast(context,"~暂无更多数据~",Toast.LENGTH_SHORT);
                    pageNum--;
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
                    loadDialog.dismiss();
                }
                LogUtil.d(TAG,"-----getUserPaopaoList Error--code="+i+"____"+s);
                if(pageNum > 0){
                    pageNum--;
                }
                if(mRefreshType==RefreshType.REFRESH){
                    LogUtil.d(TAG,"-----getUserPaopaoList Error--RefreshType.REFRESH");
                    swipeRefreshLayout.setRefreshing(false);
                }else if(mRefreshType==RefreshType.LOAD_MORE){
                    LogUtil.d(TAG, "-----getUserPaopaoList Error--RefreshType.LOAD_MORE");
                    swipeRefreshLayout.setLoading(false);
                }
            }
        });
    }

    /**
     * actionbar style
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void setActionBar(){
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
