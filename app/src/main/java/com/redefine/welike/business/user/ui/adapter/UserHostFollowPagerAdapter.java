package com.redefine.welike.business.user.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gongguan on 2018/1/17.
 */

public class UserHostFollowPagerAdapter extends FragmentPagerAdapter {
    private String[] mTitles;
    private List<Fragment> mPages = new ArrayList<>();

    public UserHostFollowPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setTitle(String[] title) {
        mTitles = title;
    }

    public void setPages(List<Fragment> pages) {
        mPages.clear();
        mPages.addAll(pages);
    }

    @Override
    public Fragment getItem(int position) {
        return mPages.get(position);
    }

    @Override
    public int getCount() {
        return mPages.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

}
