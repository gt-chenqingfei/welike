package com.redefine.welike.frameworkmvvm;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.BasePage;

/**
 * Created by mengnan on 2018/6/14.
 **/
public abstract class MvvmFragmentPageAdapter extends PagerAdapter {
    private static final String TAG = "FragmentPagerAdapter";
    private static final boolean DEBUG = false;
    protected final BaseLifecyclePageStackManager mPageStackManager;
    protected BaseLifecycleFragmentPage mCurrentPrimaryItem = null;

    public MvvmFragmentPageAdapter(BaseLifecyclePageStackManager fm) {
        this.mPageStackManager = fm;
    }

    public abstract BaseLifecycleFragmentPage getItem(int position);

    public void startUpdate(ViewGroup container) {

    }

    public Object instantiateItem(ViewGroup container, int position) {

        BaseLifecycleFragmentPage fragment = this.getItem(position);
        if (fragment != null) {
            if (fragment.getView() != null) {
                fragment.attach(container);
            } else {
                fragment.createAndAttach(container);
            }
        } else {
            fragment = new BaseLifecycleNoneFragmentPage(mPageStackManager, new PageConfig.Builder(BaseLifecycleNonePage.class).build(), null);
            fragment.createAndAttach(container);
        }
        return fragment;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        if (object instanceof BaseLifecycleFragmentPage) {
            BaseLifecycleFragmentPage fragmentPage = (BaseLifecycleFragmentPage) object;
            fragmentPage.pageStateChanged(fragmentPage.getPageState(), BasePage.PAGE_STATE_DESTROY);
            fragmentPage.destroy(container);
        }
    }

    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (object instanceof BaseLifecycleFragmentPage) {
            BaseLifecycleFragmentPage fragment = (BaseLifecycleFragmentPage) object;
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
        return ((BaseLifecycleFragmentPage) object).getView() == view;
    }

    public Parcelable saveState() {
        return null;
    }

    public void restoreState(Parcelable state, ClassLoader loader) {
    }
}
