package com.yeming.paopao.fmt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
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
import com.yeming.paopao.app.YmApplication;
import com.yeming.paopao.aty.PaopaoDetailActivity;
import com.yeming.paopao.bean.Paopao;
import com.yeming.paopao.bean.User;
import com.yeming.paopao.commons.Constant;
import com.yeming.paopao.commons.DataInfoCache;
import com.yeming.paopao.utils.LogUtil;
import com.yeming.paopao.views.MySwipeRefreshLayout;
import com.yeming.paopao.views.ToastView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-02-04 22:39
 * version: V1.0
 * Description:  朋友圈泡泡页面
 */
public class FriendFragment extends Fragment {

    private String TAG = "FriendFragment" ;
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
    private boolean isCache = true ;

    public FriendFragment(Context context){
        this.context = context ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.friend_fmt_layout,null) ;
        mList = new ArrayList<Paopao>() ;
        initView();

        //getAllFriendPaopao();
        //  缓存数据为空则去网络加载
        mList = DataInfoCache.loadFriendListPaopaos(context) ;
        if(mList.size() == 0){
            isCache = false ;
            getAllFriendPaopao();
        }


        adapter = new PaopaoFtmListAdapter(context,mList,imageLoader) ;
        AnimationAdapter animAdapter = new ScaleInAnimationAdapter(adapter);
        animAdapter.setAbsListView(listView);
        animAdapter.setInitialDelayMillis(300);
        listView.setAdapter(animAdapter);

        initListener();
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
                getAllFriendPaopao();

            }
        });

        swipeRefreshLayout.setOnLoadListener(new MySwipeRefreshLayout.OnLoadListener() {

            @Override
            public void onLoadMore() {
                LogUtil.d("FriendFragment", "---------onLoadMore---------");
                /*if(noMore){  //   没有数据，不执行查询
                    return ;
                }*/
                if(isCache && pageNum == 0){
                    pageNum = pageNum + 1 ;
                }
                ToastView.showToast(context, "正在加载。。。", Toast.LENGTH_SHORT);
                mRefreshType = RefreshType.LOAD_MORE;
                swipeRefreshLayout.setLoading(true);
                getAllFriendPaopao();
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
     *加载关注的人泡泡列表
     */
    public void getAllFriendPaopao(){
        /*if(pageNum == 0 && mRefreshType != RefreshType.REFRESH){     //刷新){
            mList = DataInfoCache.loadFriendListPaopaos(context) ;
            if(mList.size() != 0){
                return ;
            }
        }*/
        //  缓存数据为空则去网络加载
        LogUtil.d(TAG, "-----getAllFriendPaopao form net-----");
        User user = YmApplication.getCurrentUser() ;
        final BmobQuery<Paopao> query = new BmobQuery<Paopao>();
        BmobQuery<User> innerQuery = new BmobQuery<User>();
        innerQuery.addWhereRelatedTo("focus", new BmobPointer(user)); // 条件：查询当前用户关注的人
        innerQuery.findObjects(context,new FindListener<User>() {
            @Override
            public void onSuccess(List<User> users) {
                // 获取所关注的人的id封装成数组
                String[] id = new String[users.size()];
                for (int i = 0; i < users.size(); i++) {
                    id[i] = users.get(i).getObjectId();
                }
                query.addWhereContainedIn("user", Arrays.asList(id)); // 条件：查询用户id与上述数组中id匹配的paopao
                query.include("user"); // 条件：获取paopao，同时也包括它们关联的用户
                query.order("-createdAt") ;
                query.setLimit(Constant.PAGE_SIZE);
                BmobDate date = new BmobDate(new Date(System.currentTimeMillis()));
                query.addWhereLessThan("createdAt", date);
                query.setSkip(Constant.PAGE_SIZE*(pageNum++));
                query.findObjects(context,new FindListener<Paopao>() {
                    @Override
                    public void onSuccess(List<Paopao> paopaos) {
                        LogUtil.d(TAG,"-----getAllFriendPaopao onSuccess----");
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
                            //    ToastView.showToast(context,"~已加载完所有数据~",Toast.LENGTH_SHORT);
                            }
                            mList.addAll(paopaos) ;
                            adapter.setList(mList);
                            adapter.notifyDataSetChanged();
                            if(pageNum == 1){  //  加载完第一页数据后则缓存，不管是刷新还是加载操作
                                DataInfoCache.saveFriendListPaopaos(context, (ArrayList<Paopao>) mList);
                            }
                        }else {
                            LogUtil.d(TAG, "---getAllFriendPaopao Success----暂无更多数据");
                            ToastView.showToast(context, "~暂无更多数据~", Toast.LENGTH_SHORT);
                            pageNum--;
                            // 设置状态
                            if (mRefreshType == RefreshType.REFRESH) {
                                swipeRefreshLayout.setRefreshing(false);
                            } else if (mRefreshType == RefreshType.LOAD_MORE) {
                                swipeRefreshLayout.setLoading(false);
                            }
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        LogUtil.d(TAG,"-----getAllFriendPaopao Error--code="+i+"____"+s);
                        if(pageNum > 0){
                            pageNum--;
                        }
                        if(mRefreshType==RefreshType.REFRESH){
                            LogUtil.d(TAG,"-----getAllFriendPaopao Error--RefreshType.REFRESH");
                            swipeRefreshLayout.setRefreshing(false);
                        }else if(mRefreshType==RefreshType.LOAD_MORE){
                            LogUtil.d(TAG, "-----getAllFriendPaopao Error--RefreshType.LOAD_MORE");
                            swipeRefreshLayout.setLoading(false);
                        }
                    }
                });
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG,"-----getAllFriendPaopao Error--code="+i+"____"+s);
                if(pageNum > 0){
                    pageNum--;
                }
                if(mRefreshType==RefreshType.REFRESH){
                    LogUtil.d(TAG,"-----getAllFriendPaopao Error--RefreshType.REFRESH");
                    swipeRefreshLayout.setRefreshing(false);
                }else if(mRefreshType==RefreshType.LOAD_MORE){
                    LogUtil.d(TAG, "-----getAllFriendPaopao Error--RefreshType.LOAD_MORE");
                    swipeRefreshLayout.setLoading(false);
                }
            }
        });
    }
}
