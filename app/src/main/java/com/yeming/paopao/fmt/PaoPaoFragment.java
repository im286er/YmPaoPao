package com.yeming.paopao.fmt;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yeming.paopao.R;
import com.yeming.paopao.adapter.PaopaoFtmListAdapter;
import com.yeming.paopao.aty.EditPaopaoActivity;
import com.yeming.paopao.aty.PaopaoDetailActivity;
import com.yeming.paopao.bean.Paopao;
import com.yeming.paopao.commons.Constant;
import com.yeming.paopao.commons.DataInfoCache;
import com.yeming.paopao.utils.LogUtil;
import com.yeming.paopao.utils.SharedPreHelperUtil;
import com.yeming.paopao.views.MySwipeRefreshLayout;
import com.yeming.paopao.views.ToastView;
import com.yeming.paopao.views.third.DialogView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-28 21:27
 * version: V1.0
 * Description: 泡泡广场页面
 */
public class PaoPaoFragment extends Fragment {

    private String TAG = "PaoPaoFragment" ;
    private View rootView ;
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

    private boolean isCache = true ;

    public PaoPaoFragment(Context context){
        this.context = context ;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.paopao_fmt_layout,null) ;

        mList = new ArrayList<Paopao>() ;
        loadDialog = DialogView.loadDialog(context, R.string.loading) ;
        initView();

      //  getAllPaopao();
        mList = DataInfoCache.loadListPaopaos(context) ;
        if(mList.size() == 0){  //  缓存数据为空则去网络加载
            isCache = false ;
            loadDialog.show();
            getAllPaopao();
        }

        adapter = new PaopaoFtmListAdapter(context,mList,imageLoader) ;
        AnimationAdapter animAdapter = new ScaleInAnimationAdapter(adapter);
        animAdapter.setAbsListView(listView);
        animAdapter.setInitialDelayMillis(300);
        listView.setAdapter(animAdapter);

        initListener();
        //注册广播
        IntentFilter filter = new IntentFilter() ;
        filter.addAction(Constant.USER_NICK_CHANGE);
        filter.addAction(Constant.USER_AVATER_CHANGE);
        context.registerReceiver(broadcastReceiver,filter) ;

        return rootView ;
    }

    /**
     *
     */
    public void initView(){
        swipeRefreshLayout = (MySwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.green, R.color.green, R.color.green, R.color.green);
        listView = (ListView) rootView.findViewById(R.id.listView);
    }

    /**
     *
     */
    public void initListener(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNum = 0 ;
                mRefreshType = RefreshType.REFRESH;
                swipeRefreshLayout.setRefreshing(true);
                getAllPaopao();

            }
        });

        swipeRefreshLayout.setOnLoadListener(new MySwipeRefreshLayout.OnLoadListener() {

            @Override
            public void onLoadMore() {
                LogUtil.d("PictrueFragment", "---------onLoadMore---------");
                /*if(noMore){  //   没有数据，不执行查询
                    return ;
                }*/
                if(isCache && pageNum == 0){
                    pageNum = pageNum + 1 ;
                }
                ToastView.showToast(context, "正在加载。。。", Toast.LENGTH_SHORT);
                mRefreshType = RefreshType.LOAD_MORE;
                swipeRefreshLayout.setLoading(true);
                getAllPaopao();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent() ;
                Paopao paopao = mList.get(i) ;
                intent.putExtra("paopao_detail",paopao) ;
                intent.setClass(getActivity(), PaopaoDetailActivity.class) ;
                startActivity(intent);
               // startActivityForResult(intent,REQUEST_EDIT_MAOPAO);
                //getActivity().startActivityForResult(intent, REQUEST_EDIT_MAOPAO);
               /* context.overridePendingTransition(R.anim.alpha,
                        R.anim.alpha2);*/
            }
        });
    }

    /**
     *加载泡泡列表
     */
    public void getAllPaopao(){
        //  加载缓存
        /*if(pageNum == 0 && mRefreshType != RefreshType.REFRESH ){
            loadDialog.show();
            LogUtil.d(TAG, "-----getAllPaopao loadDialog show-----");
            mList = DataInfoCache.loadListPaopaos(context) ;
            if(mList.size() != 0){
                if(loadDialog.isShowing()){
                    LogUtil.d(TAG, "-----getAllPaopao loadDialog dismiss 1-----");
                    loadDialog.dismiss();
                }
                return ;
            }
        }*/
        //  缓存数据为空则去网络加载
        LogUtil.d(TAG, "-----getAllPaopao form net-----");
        BmobQuery<Paopao> query = new BmobQuery<Paopao>() ;
        query.order("-createdAt") ;
        query.setLimit(Constant.PAGE_SIZE);
        BmobDate date = new BmobDate(new Date(System.currentTimeMillis()));
        query.addWhereLessThan("createdAt", date);
        query.setSkip(Constant.PAGE_SIZE*(pageNum++));
        query.include("user");
        query.findObjects(context,new FindListener<Paopao>() {
            @Override
            public void onSuccess(List<Paopao> paopaos) {

                if(loadDialog.isShowing()){
                    LogUtil.d(TAG, "-----getAllPaopao loadDialog dismiss 2-----");
                    loadDialog.dismiss();
                }

                LogUtil.d(TAG,"---getAllPaopao Success----"+paopaos.size());
                if(paopaos.size()!=0&&paopaos.get(paopaos.size()-1)!=null){
                    if(mRefreshType==RefreshType.REFRESH){     //刷新
                        mList.clear();
                        swipeRefreshLayout.setRefreshing(false);// 设置状态
                        //  每次刷新都缓存第一页数据
                        DataInfoCache.saveListPaopaos(context, (ArrayList<Paopao>) paopaos);
                    }else if(mRefreshType==RefreshType.LOAD_MORE){ //  加载更多
                        swipeRefreshLayout.setLoading(false);// 设置状态
                    }
                    if(paopaos.size()<Constant.PAGE_SIZE){
                        ToastView.showToast(context,"~已加载完所有数据~",Toast.LENGTH_SHORT);
                    }
                    mList.addAll(paopaos) ;
                    adapter.setList(mList);
                    adapter.notifyDataSetChanged();
                    if(pageNum == 1){  //  加载完第一页数据后则缓存，不管是刷新还是加载操作
                        DataInfoCache.saveListPaopaos(context, (ArrayList<Paopao>) mList);
                    }
                }else{
                    LogUtil.d(TAG,"---getAllPaopao Success----暂无更多数据");
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
                LogUtil.d(TAG,"-----getAllPaopao Error--code="+i+"____"+s);
                if(pageNum > 0){
                    pageNum--;
                }
                if(mRefreshType==RefreshType.REFRESH){
                    LogUtil.d(TAG,"-----getAllPaopao Error--RefreshType.REFRESH");
                    swipeRefreshLayout.setRefreshing(false);
                }else if(mRefreshType==RefreshType.LOAD_MORE){
                    LogUtil.d(TAG, "-----getAllPaopao Error--RefreshType.LOAD_MORE");
                    swipeRefreshLayout.setLoading(false);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_EDIT_MAOPAO){
            if(resultCode == Activity.RESULT_OK || data != null){
                Paopao paopao = (Paopao) data.getSerializableExtra("add_paopao");
                mList.add(0,paopao);
                DataInfoCache.saveListPaopaos(context, (ArrayList<Paopao>) mList);
                //  重新设置adapter，避免第一条数据和第二条数据用的还是listview缓存
                adapter = new PaopaoFtmListAdapter(context,mList,imageLoader) ;
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                listView.setSelection(0);
                //  本地更新保存的泡泡数量
                int num = SharedPreHelperUtil.getInstance(context).getUserPaopaoNum() ;
                SharedPreHelperUtil.getInstance(context).setUserPaopaoNum(num+1);
                // 发送更新泡泡数的广播
                Intent intent = new Intent() ;
                intent.setAction(Constant.USER_PAOPAONUM_CHANGE) ;
                context.sendBroadcast(intent);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
          inflater.inflate(R.menu.paopao_edit_action,menu);
          super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId() ;
        switch (id){
            case R.id.action_edit:
                Intent intent = new Intent() ;
                intent.setClass(getActivity(), EditPaopaoActivity.class) ;
                startActivityForResult(intent, REQUEST_EDIT_MAOPAO);
                /*((Activity) context).overridePendingTransition(R.anim.alpha,
                        R.anim.alpha2);*/
                break ;
            default:break ;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(broadcastReceiver);
    }

    /**
     * 昵称修改完成广播，更新页面数据
     */
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction() ;
            LogUtil.d(TAG,"-----action paopao---"+action);
            if(action.equals(Constant.USER_NICK_CHANGE) || action.equals(Constant.USER_AVATER_CHANGE)){
                pageNum = 0 ;
                mRefreshType = RefreshType.REFRESH;
                getAllPaopao();
            }
        }
    } ;
}
