package com.yeming.paopao.fmt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yeming.paopao.R;
import com.yeming.paopao.adapter.MainFtmAdapter;
import com.yeming.paopao.aty.MainActivity;
import com.yeming.paopao.utils.LogUtil;
import com.yeming.paopao.views.third.PagerSlidingTabStrip;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-28 20:36
 * version: V1.0
 * Description:
 */
public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment" ;
    private View contentView;
    private PagerSlidingTabStrip tabs;
    private ViewPager contentPager;
    private MainFtmAdapter mainFtmAdapter ;
    private MainActivity mainActivity ;

    @SuppressLint("ValidFragment")
    public MainFragment(MainActivity mainActivity){
        this.mainActivity = mainActivity ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.main_fmt_layout,null) ;

        initView();
        setPageContent();

        return contentView ;
    }

    public void initView(){
        contentPager = (ViewPager) contentView.findViewById(R.id.content_pager);
        tabs = (PagerSlidingTabStrip) contentView.findViewById(R.id.tabs);
    }

    /**
     * 设置viewpage
     */
    public void setPageContent(){
        mainFtmAdapter = new MainFtmAdapter(mainActivity.getSupportFragmentManager(),mainActivity) ;
        contentPager.setAdapter(mainFtmAdapter);
       // contentPager.setPageTransformer(true,new ZoomOutPageTransformer());
        contentPager.setOffscreenPageLimit(2);  //  设置viewPage缓存三个页面
        tabs.setViewPager(contentPager);
    }

    @Override
    public void onAttach(Activity activity) {
        mainActivity = (MainActivity) activity;
        LogUtil.d(TAG,"--------onAttach---------");
        super.onAttach(activity);
    }
}
