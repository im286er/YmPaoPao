package com.yeming.paopao.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.yeming.paopao.fmt.FriendFragment;
import com.yeming.paopao.fmt.PaoPaoFragment;
import com.yeming.paopao.fmt.PictrueFragment;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-28 21:32
 * version: V1.0
 * Description:
 */
public class MainFtmAdapter extends FragmentStatePagerAdapter {

    private Context context ;
    private String Title[] = { "PaoPao", "Pictrue", "Focus" };

    public MainFtmAdapter(FragmentManager fm,Context context) {
        super(fm);
        this.context = context ;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null ;
        if(position == 0){
            fragment = new PaoPaoFragment(context);
        }else if(position == 1){
            fragment = new PictrueFragment(context) ;
        }else if(position == 2){
            fragment =  new FriendFragment(context) ;
        }
        return fragment ;
    }

    @Override
    public int getCount() {
        return Title.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Title[position];
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return super.isViewFromObject(view, object);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public void startUpdate(ViewGroup container) {
        super.startUpdate(container);
    }
}
