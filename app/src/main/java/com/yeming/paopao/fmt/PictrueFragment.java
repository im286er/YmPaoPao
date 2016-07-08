package com.yeming.paopao.fmt;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.yeming.paopao.R;
import com.yeming.paopao.adapter.PictrueFtmGridAdapter;
import com.yeming.paopao.adapter.ViewPageAdapter;
import com.yeming.paopao.app.YmApplication;
import com.yeming.paopao.aty.MainActivity;
import com.yeming.paopao.bean.PicViewImage;
import com.yeming.paopao.bean.Pictrue;
import com.yeming.paopao.commons.Constant;
import com.yeming.paopao.commons.DataInfoCache;
import com.yeming.paopao.utils.BitmapUtil;
import com.yeming.paopao.utils.LogUtil;
import com.yeming.paopao.views.FixedSpeedScroller;
import com.yeming.paopao.views.MySwipeRefreshLayout;
import com.yeming.paopao.views.ToastView;
import com.yeming.paopao.views.third.staggeredgridview.StaggeredGridView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-28 21:29
 * version: V1.0
 * Description:   图片广场、瀑布流显示页面
 */
public class PictrueFragment extends Fragment {

    private String TAG = "PictrueFragment" ;
    //private ListView list ;
    private View contentView ;
    private Context context ;
    private MySwipeRefreshLayout swipeRefreshLayout ;
    private StaggeredGridView staggeredGridView ;
    private ViewPager viewPager;
    private String[] viewPageTitles;              // viewpage 图片标题数组
    private List<View> dots;                     // viewpage 圆点
    private TextView viewPage_title;             //viewpage 图片标题
    private static double picScale;
    private ViewPageAdapter viewPageAdapter ;   // viewpage 适配器
    private List<PicViewImage> viewImageList ;  //  viewpage 图片对象集合
    private int currentItem = 0;                // 当前选中的viewpage 图片位置
    private ScheduledExecutorService scheduledExecutorService;
    private PictrueFtmGridAdapter gridAdapter ;
    private List<Pictrue> mList ;               //  图片对象集合
    private ImageLoader imageLoader = ImageLoader.getInstance();   //  图片加载器
    private int pageNum = 0 ;    //  当前页
    public enum RefreshType{   //  加载数据操作类型  刷新 加载更多
        REFRESH,LOAD_MORE
    }
    private RefreshType mRefreshType = RefreshType.LOAD_MORE;

    private boolean isViewPageFileExit = false ; //进入页面ViewPage是否加载数据成功


    public PictrueFragment(Context context){
        this.context = context ;
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            viewPager.setCurrentItem(currentItem, true);
        };
    };

    @Override
    public void onStart() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 3, 5,
                TimeUnit.SECONDS);
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.pictrue_fmt_layout,null) ;

        viewImageList = new ArrayList<PicViewImage>() ;
        mList = new ArrayList<Pictrue>() ;
        picScale = BitmapUtil.getPicScale(getResources(), R.drawable.j_6);

        initView();
        initHeadView();

        getViewPageData();
        //getGridData();
        mList = DataInfoCache.loadGridPictrues(context) ;
        if(mList.size() == 0){  //  缓存数据为空则去网络加载
            getGridData();
        }

        gridAdapter = new PictrueFtmGridAdapter(context,imageLoader,mList) ;
        gridAdapter.notifyDataSetChanged();
        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(gridAdapter);
        swingBottomInAnimationAdapter.setAbsListView(staggeredGridView);
        swingBottomInAnimationAdapter.setInitialDelayMillis(450);
        staggeredGridView.setAdapter(swingBottomInAnimationAdapter);
        staggeredGridView.setOnScrollListener(new PauseOnScrollListener(imageLoader,
                true, true));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNum = 0 ;
                mRefreshType = RefreshType.REFRESH;
                swipeRefreshLayout.setRefreshing(true);
                getGridData();
                if(!isViewPageFileExit){
                    getViewPageData();
                }
            }
        });

        swipeRefreshLayout.setOnLoadListener(new MySwipeRefreshLayout.OnLoadListener() {

            @Override
            public void onLoadMore() {
                LogUtil.d("PictrueFragment","---------onLoadMore---------");
                ToastView.showToast(context, "正在加载。。。", Toast.LENGTH_SHORT);
                mRefreshType = RefreshType.LOAD_MORE;
                swipeRefreshLayout.setLoading(true);
                getGridData();
            }
        });

        return contentView;
    }

    @Override
    public void onStop() {
        scheduledExecutorService.shutdown();
        super.onStop();
    }

    private void initHeadView(){
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View head = layoutInflater.inflate(R.layout.pictrue_fmt_layout_viewpage,
                staggeredGridView, false);
        dots = new ArrayList<View>();
        dots.add(head.findViewById(R.id.v_dot0));
        dots.add(head.findViewById(R.id.v_dot1));
        dots.add(head.findViewById(R.id.v_dot2));
        dots.add(head.findViewById(R.id.v_dot3));
        dots.add(head.findViewById(R.id.v_dot4));
       /* dots.add(head.findViewById(R.id.v_dot5));*/
        //dots.add(head.findViewById(R.id.v_dot6));
        viewPage_title = (TextView) head.findViewById(R.id.tv_title);
        viewPage_title.setTypeface(YmApplication.chineseTypeface);
        viewPage_title.setText("");
        viewPager = (ViewPager) head.findViewById(R.id.vp) ;
        RelativeLayout.LayoutParams viewPaLayoutParams = new RelativeLayout.LayoutParams(
                MainActivity.screenWidthDip,
                (int) (MainActivity.screenWidthDip * picScale));
        viewPager.setLayoutParams(viewPaLayoutParams);
        setViewPagerScrollSpeed();
        viewPageAdapter = new ViewPageAdapter(viewImageList,getActivity()) ;
        viewPager.setAdapter(viewPageAdapter);
        viewPager.setOnPageChangeListener(new MyPageChangeListener());
        staggeredGridView.addHeaderView(head);
    }

    public void initView(){
        swipeRefreshLayout = (MySwipeRefreshLayout) contentView.findViewById(R.id.swipeRefreshLayout);
        staggeredGridView = (StaggeredGridView) contentView.findViewById(R.id.grid_view);
        swipeRefreshLayout.setColorSchemeResources(R.color.green, R.color.green, R.color.green, R.color.green);
    }

    /**
     * 获取viewpage 图片数据
     */
    public void getViewPageData(){
        BmobQuery<PicViewImage> query = new BmobQuery<PicViewImage>() ;
        query.order("-createdAt") ;
        query.setLimit(Constant.VIEWPAGER_SIZE);
        query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 先从缓存获取数据，如果没有，再从网络获取。
        query.findObjects(getActivity(),new FindListener<PicViewImage>() {
            @Override
            public void onSuccess(List<PicViewImage> picViewImages) {
                LogUtil.d(TAG,"---getViewPageData Success----"+picViewImages.size());
                viewImageList = picViewImages ;
                viewPageAdapter.setList(viewImageList);
                viewPageAdapter.notifyDataSetChanged();
                isViewPageFileExit = true ;
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG,"-----getViewPageData Error--code="+i+"____"+s);
                isViewPageFileExit = false ;
            }
        });
    }

    /**
     * 获取grid图片数据
     */
    public void getGridData(){
        BmobQuery<Pictrue> query = new BmobQuery<Pictrue>() ;
        query.order("-createdAt") ;
        query.setLimit(Constant.PIC_PAGE_SIZE);
        //query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);  // 使用自己的缓存策略
        BmobDate date = new BmobDate(new Date(System.currentTimeMillis()));
        query.addWhereLessThan("createdAt", date);
        LogUtil.d(TAG,"---getGridData Success----pageNum="+pageNum);
        query.setSkip(Constant.PIC_PAGE_SIZE*(pageNum++));
        LogUtil.d(TAG,"---getGridData Success----Skip="+Constant.PIC_PAGE_SIZE*(pageNum));
       // query.include("user");  //  需放开
        query.findObjects(getActivity(),new FindListener<Pictrue>() {
            @Override
            public void onSuccess(List<Pictrue> pictrues) {
               if(pictrues.size()!=0&&pictrues.get(pictrues.size()-1)!=null){
                   if(mRefreshType==RefreshType.REFRESH){     //刷新
                       mList.clear();
                       swipeRefreshLayout.setRefreshing(false);// 设置状态
                       //  每次刷新都缓存第一页数据
                   //    DataInfoCache.saveGridPictrues(context, (ArrayList<Pictrue>) pictrues);
                   }else if(mRefreshType==RefreshType.LOAD_MORE){ //  加载更多
                       swipeRefreshLayout.setLoading(false);// 设置状态
                   }
                   if(pictrues.size()<Constant.PIC_PAGE_SIZE){
                       ToastView.showToast(context,"~已加载完所有数据~",Toast.LENGTH_SHORT);
                   }
                   mList.addAll(pictrues) ;
                   gridAdapter.setList(mList);
                   gridAdapter.notifyDataSetChanged();
                   if(pageNum == 1){  //  加载完第一页数据后则缓存，不管是刷新还是加载操作
                       DataInfoCache.saveGridPictrues(context, (ArrayList<Pictrue>) pictrues);
                   }
               }else{
                   LogUtil.d(TAG,"---getGridData Success----暂无更多数据");
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
                LogUtil.d(TAG,"-----getGridData Error--code="+i+"____"+s);
                if(pageNum > 0){
                    pageNum--;
                }
                if(mRefreshType==RefreshType.REFRESH){
                    LogUtil.d(TAG,"-----getGridData Error--RefreshType.REFRESH");
                    swipeRefreshLayout.setRefreshing(false);
                }else if(mRefreshType==RefreshType.LOAD_MORE){
                    LogUtil.d(TAG,"-----getGridData Error--RefreshType.LOAD_MORE");
                    swipeRefreshLayout.setLoading(false);
                }
            }
        });
    }

    private class ScrollTask implements Runnable {

        public void run() {
            synchronized (viewPager) {
                currentItem = (currentItem + 1) % Constant.VIEWPAGER_SIZE;
                handler.obtainMessage().sendToTarget();
            }
        }

    }

    /**
     * 设置ViewPager的滑动速度
     *
     * */
    private void setViewPagerScrollSpeed() {
        try {
            Field mScroller = null;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(
                    viewPager.getContext());
            mScroller.set(viewPager, scroller);
        } catch (NoSuchFieldException e) {

        } catch (IllegalArgumentException e) {

        } catch (IllegalAccessException e) {

        }
    }

    private Animator mCurrentAnimator;
    /**
     * viewPage 监听器
     */
    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {

        private int oldPosition = 0;

        @Override
        public void onPageScrolled(int i, float v, int i2) {

        }

        @Override
        public void onPageSelected(final int i) {
            currentItem = i;

            //  文字透明渐变显示动画
            /*Animation animation = new AlphaAnimation(1.0f, 0);
            animation.setDuration(300);
            animation.setInterpolator(new DecelerateInterpolator());
            animation.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // TODO Auto-generated method stub

                    if(viewImageList.size() != 0){
                        viewPage_title.setText(viewImageList.get(i).getImageTips());
                    }else{
                        viewPage_title.setText("");
                    }

                    Animation animation1 = new AlphaAnimation(0, 1.0f);
                    animation1.setDuration(300);
                    animation1.setInterpolator(new AccelerateInterpolator());
                    viewPage_title.startAnimation(animation1);
                }
            });
            viewPage_title.startAnimation(animation);
            dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
            dots.get(i).setBackgroundResource(R.drawable.dot_focus);
            oldPosition = i;*/

            //  文字缩放显示动画
            AnimatorSet set = new AnimatorSet();
            set.play(ObjectAnimator.ofFloat(viewPage_title, View.ALPHA, 1.0f, 0.0f))
                    .with(ObjectAnimator.ofFloat(viewPage_title, View.SCALE_X, 1.0f,
                            0.5f))
                    .with(ObjectAnimator.ofFloat(viewPage_title, View.SCALE_Y, 1.0f,
                            0.5f));

            set.setDuration(300);
            set.setInterpolator(new DecelerateInterpolator());
            set.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationCancel(Animator arg0) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationEnd(Animator arg0) {
                    // TODO Auto-generated method stub
                    mCurrentAnimator = null;
                    AnimatorSet set = new AnimatorSet();
                    if(viewImageList.size() != 0){
                        viewPage_title.setText(viewImageList.get(i).getImageTips());
                    }else{
                        viewPage_title.setText("");
                    }
                    set.play(
                            ObjectAnimator.ofFloat(viewPage_title, View.ALPHA, 0.0f,
                                    1.0f))
                            .with(ObjectAnimator.ofFloat(viewPage_title,
                                    View.SCALE_X, 0.5f, 1.0f))
                            .with(ObjectAnimator.ofFloat(viewPage_title,
                                    View.SCALE_Y, 0.5f, 1.0f));
                    set.setDuration(300);
                    set.setInterpolator(new DecelerateInterpolator());
                    set.start();
                }

                @Override
                public void onAnimationRepeat(Animator arg0) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationStart(Animator arg0) {
                    // TODO Auto-generated method stub

                }

            });
            set.start();
            mCurrentAnimator = set;
            dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
            dots.get(i).setBackgroundResource(R.drawable.dot_focus);
            oldPosition = i;
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    }
}
