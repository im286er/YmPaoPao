package com.yeming.paopao.views;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ListView;

import com.yeming.paopao.R;
import com.yeming.paopao.utils.LogUtil;
import com.yeming.paopao.views.third.staggeredgridview.StaggeredGridView;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-31 21:57
 * version: V1.0
 * Description:
 */
public class MySwipeRefreshLayout extends SwipeRefreshLayout implements AbsListView.OnScrollListener {

    private String TAG = "MySwipeRefreshLayout" ;
    /**
     * 滑动到最下面时的上拉操作
     */

    private int mTouchSlop;
    /**
     * StaggeredGridView
     */
    private StaggeredGridView mListView;

    /**
     * listview实例
     */
    private ListView mListView1 ;

    /**
     *
     */
    private boolean isListViewType = false ;

    /**
     * 上拉监听器, 到了最底部的上拉加载操作
     */
    private OnLoadListener mOnLoadListener;

    /**
     * ListView的加载中footer
     */
    private View mListViewFooter;

    /**
     * 按下时的y坐标
     */
    private int mYDown;
    /**
     * 抬起时的y坐标, 与mYDown一起用于滑动到底部时判断是上拉还是下拉
     */
    private int mLastY;
    /**
     * 是否在加载中 ( 上拉加载更多 )
     */
    private boolean isLoading = false;


    public MySwipeRefreshLayout(Context context) {
        super(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mListViewFooter = LayoutInflater.from(context).inflate(R.layout.swipe_foot_view, null,
                false);
    }

    public MySwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mListViewFooter = LayoutInflater.from(context).inflate(R.layout.swipe_foot_view, null,
                false);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 初始化ListView对象
        if (mListView == null || mListView1 == null) {
            getListView();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 按下
                mYDown = (int) event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                // 移动
                mLastY = (int) event.getRawY();
                break;

            case MotionEvent.ACTION_UP:
                // 抬起
                if (canLoad()) {
                    loadData();
                }
                break;
            default:
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    /**
     * 是否可以加载更多, 条件是到了最底部, listview不在加载中, 且为上拉操作.
     *
     * @return
     */
    private boolean canLoad() {
        return isBottom() && !isLoading && isPullUp();
    }

    /**
     * 判断是否到了最底部
     */
    private boolean isBottom() {

        if (mListView != null && mListView.getAdapter() != null && !isListViewType) {
            LogUtil.d(TAG,"-------StaggeredGridView--isBottom---");
            return mListView.getLastVisiblePosition() == (mListView.getAdapter().getCount() - 1);
        }else if(mListView1 != null && mListView1.getAdapter() != null && isListViewType){
            LogUtil.d(TAG,"------ListView---isBottom---");
            return mListView1.getLastVisiblePosition() == (mListView1.getAdapter().getCount() - 1);
        }
        return false;
    }

    /**
     * 是否是上拉操作
     *
     * @return
     */
    private boolean isPullUp() {
        return (mYDown - mLastY) >= mTouchSlop;
    }

    /**
     * 如果到了最底部,而且是上拉操作.那么执行onLoad方法
     */
    private void loadData() {
        if (mOnLoadListener != null) {
            // 设置状态
            setLoading(true);
            mOnLoadListener.onLoadMore();
        }
    }

    /**
     * @param loading
     */
    public void setLoading(boolean loading) {
        isLoading = loading;
        if (isLoading) {
            /*if(isListViewType){
                mListView1.addFooterView(mListViewFooter);
            }*/
        } else {
            /*if(isListViewType){
                mListView1.removeFooterView(mListViewFooter);
            }*/
            mYDown = 0;
            mLastY = 0;
        }
    }

    /**
     * @param onLoadListener
     */
    public void setOnLoadListener(OnLoadListener onLoadListener) {
        mOnLoadListener = onLoadListener;
    }

    /**
     * 获取ListView对象
     */
    private void getListView() {
        int childs = getChildCount();
        if (childs > 0) {
            View childView = getChildAt(0);
            if (childView instanceof StaggeredGridView) {
                mListView = (StaggeredGridView) childView;
                // 设置滚动监听器给ListView, 使得滚动的情况下也可以自动加载
                mListView.setOnScrollListener(this);
                isListViewType = false ;
                LogUtil.d(TAG,"---------找到StaggeredGridView");
            }else if(childView instanceof ListView){
                mListView1 = (ListView) childView ;
                mListView1.setOnScrollListener(this);
                isListViewType = true ;
                LogUtil.d(TAG,"---------找到ListView");
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        // 滚动时到了最底部也可以加载更多
        LogUtil.d(TAG,"---------onScroll");
        if (canLoad()) {
            loadData();
        }
    }

    /**
     * 加载更多的监听器
     */
    public static interface OnLoadListener {
        public void onLoadMore();
    }
}
