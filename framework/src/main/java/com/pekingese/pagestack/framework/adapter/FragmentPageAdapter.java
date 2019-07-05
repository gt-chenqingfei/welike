package com.pekingese.pagestack.framework.adapter;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.BasePage;
import com.pekingese.pagestack.framework.page.BaseFragmentPage;
import com.pekingese.pagestack.framework.page.NoneFragmentPage;

/**
 * Created by liwenbo on 2018/2/3.
 */

public abstract class FragmentPageAdapter extends PagerAdapter {
    private static final String TAG = "FragmentPagerAdapter";
    private static final boolean DEBUG = false;
    protected final IPageStackManager mPageStackManager;
    protected BaseFragmentPage mCurrentPrimaryItem = null;

    public FragmentPageAdapter(IPageStackManager fm) {
        this.mPageStackManager = fm;
    }

    public abstract BaseFragmentPage getItem(int position);

    public void startUpdate(ViewGroup container) {

    }

    public Object instantiateItem(ViewGroup container, int position) {

        BaseFragmentPage fragment = this.getItem(position);
        if (fragment != null) {
            if (fragment.getView() != null) {
                fragment.attach(container);
            } else {
                fragment.createAndAttach(container);
            }
        } else {
            fragment = new NoneFragmentPage(mPageStackManager, new PageConfig.Builder(NoneFragmentPage.class).build(), null);
            fragment.createAndAttach(container);
        }
        return fragment;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        if (object instanceof BaseFragmentPage) {
            BaseFragmentPage fragmentPage = (BaseFragmentPage) object;
            fragmentPage.pageStateChanged(fragmentPage.getPageState(), BasePage.PAGE_STATE_DESTROY);
            fragmentPage.destroy(container);
        }
    }

    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (object instanceof BaseFragmentPage) {
            BaseFragmentPage fragment = (BaseFragmentPage) object;
            if (fragment != this.mCurrentPrimaryItem) {
                if (this.mCurrentPrimaryItem != null) {
                    mCurrentPrimaryItem.pageStateChanged(mCurrentPrimaryItem.getPageState(), BasePage.PAGE_STATE_HIDE);
                }
                fragment.pageStateChanged(fragment.getPageState(), BasePage.PAGE_STATE_SHOW);
                this.mCurrentPrimaryItem = fragment;
            }

        }
    }

    public void finishUpdate(ViewGroup container) {

    }

    public boolean isViewFromObject(View view, Object object) {
        return ((BaseFragmentPage) object).getView() == view;
    }

    public Parcelable saveState() {
        return null;
    }

    public void restoreState(Parcelable state, ClassLoader loader) {
    }

}
