package com.redefine.welike.business.search.ui.page;

import android.view.ViewGroup;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.BaseFragmentPage;
import com.redefine.welike.business.search.ui.contract.ISearchPageContract;

/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 18/1/8
 * <p>
 * Description : TODO
 * <p>
 * Creation    : 18/1/8
 * Author      : liwenbo0328@163.com
 * History     : Creation, 18/1/8, liwenbo, Create the file
 * ****************************************************************************
 */

public class SearchFragmentPageSwitcher {
    private final IPageStackManager mStackManager;
    private SearchPostFragmentPage mSearchPostFragmentPage;
    private SearchUserFragmentPage mSearchUserFragmentPage;
    private SearchLatestFragmentPage mSearchLatestFragmentPage;

    private BaseFragmentPage mLastShowFragment;
    private int mLastIndex = -1;

    public static final int FRAGMENT_LATEST_POSITION = 0;
    public static final int FRAGMENT_POST_POSITION = 1;
    public static final int FRAGMENT_USER_POSITION = 2;
    private ISearchPageContract.ISearchResultPagePresenter mPresenter;


    public SearchFragmentPageSwitcher(IPageStackManager fragmentManager, ISearchPageContract.ISearchResultPagePresenter presenter) {
        mStackManager = fragmentManager;
        mPresenter = presenter;
    }

    public void setCurrentItem(ViewGroup viewGroup, int index) {
        if (mLastIndex == index) {
            return;
        }
        mLastIndex = index;
        // Do we already have this fragment?
        BaseFragmentPage fragment = findFragmentByIndex(index);
        if (mLastShowFragment != null) {
            mLastShowFragment.detach(viewGroup);
        }
        if (fragment != null) {
            if (fragment.getView() == null) {
                fragment.createAndAttach(viewGroup);
            } else {
                fragment.attach(viewGroup);
            }
        } else {
            fragment = getFragmentByIndex(index);
            fragment.createAndAttach(viewGroup);
        }
        mLastShowFragment = fragment;
    }


    private BaseFragmentPage findFragmentByIndex(int index) {
        BaseFragmentPage fragment;
        switch (index) {
            case FRAGMENT_POST_POSITION:
                fragment = mSearchPostFragmentPage;
                break;
            case FRAGMENT_USER_POSITION:
                fragment = mSearchUserFragmentPage;
                break;
            case FRAGMENT_LATEST_POSITION:
                fragment = mSearchLatestFragmentPage;
                break;
            default:
                fragment = mSearchPostFragmentPage;
                break;

        }
        return fragment;
    }


    private BaseFragmentPage getFragmentByIndex(int index) {
        BaseFragmentPage fragment;
        switch (index) {
            case FRAGMENT_POST_POSITION:
                if (mSearchPostFragmentPage == null) {
                    mSearchPostFragmentPage = new SearchPostFragmentPage(mStackManager, new PageConfig.Builder(SearchPostFragmentPage.class).build(), null);
                }
                fragment = mSearchPostFragmentPage;
                break;
            case FRAGMENT_USER_POSITION:
                if (mSearchUserFragmentPage == null) {
                    mSearchUserFragmentPage = new SearchUserFragmentPage(mStackManager, new PageConfig.Builder(SearchUserFragmentPage.class).build(), null);
                }
                fragment = mSearchUserFragmentPage;
                break;
            case FRAGMENT_LATEST_POSITION:
                if (mSearchLatestFragmentPage == null) {
                    mSearchLatestFragmentPage = new SearchLatestFragmentPage(mStackManager, new PageConfig.Builder(SearchLatestFragmentPage.class).build(), null);
                }
                mSearchLatestFragmentPage.setBasePresenter(mPresenter);
                fragment = mSearchLatestFragmentPage;
                break;
            default:
                if (mSearchPostFragmentPage == null) {
                    mSearchPostFragmentPage = new SearchPostFragmentPage(mStackManager, new PageConfig.Builder(SearchPostFragmentPage.class).build(), null);
                }
                fragment = mSearchPostFragmentPage;
                break;

        }
        return fragment;
    }

    public void onActivityResume() {
        if (mLastShowFragment != null) {
            mLastShowFragment.onActivityResume();
        }
    }

    public void onActivityPause() {
        if (mLastShowFragment != null) {
            mLastShowFragment.onActivityPause();
        }
    }

    public void destroy(ViewGroup container) {
        if (mSearchPostFragmentPage != null) {
            mSearchPostFragmentPage.destroy(container);
        }
        if (mSearchUserFragmentPage != null) {
            mSearchUserFragmentPage.destroy(container);
        }
        if (mSearchLatestFragmentPage != null) {
            mSearchLatestFragmentPage.destroy(container);
        }
    }

    public void doRealSearch(String lastText) {
        if (mLastShowFragment != null && mLastShowFragment instanceof ISearchRefreshDelegate) {
            ((ISearchRefreshDelegate) mLastShowFragment).onRefresh(lastText);
        }
    }

    public void setPresenter(ISearchPageContract.ISearchResultPagePresenter presenter) {
        mPresenter = presenter;
    }

    public static interface ISearchRefreshDelegate {
        void onRefresh(String searchText);
    }
}
