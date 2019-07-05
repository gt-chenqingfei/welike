package com.redefine.welike.business.feeds.ui.adapter;

import android.support.annotation.Nullable;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.adapter.FragmentPageAdapter;
import com.pekingese.pagestack.framework.page.BaseFragmentPage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nianguowang on 2018/4/16
 */

public class InterestCategoryDetailPageAdapter extends FragmentPageAdapter {

    private List<BaseFragmentPage> mFragmentPages = new ArrayList<>();
    private String[] mTitles;

    public InterestCategoryDetailPageAdapter(IPageStackManager fm) {
        super(fm);
    }

    public void setFragmentPage(List<BaseFragmentPage> fragmentPages) {
        mFragmentPages.clear();
        mFragmentPages.addAll(fragmentPages);
    }

    public void setTitles(String[] titles) {
        mTitles = titles;
    }

    @Override
    public BaseFragmentPage getItem(int position) {
        return mFragmentPages.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentPages.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
