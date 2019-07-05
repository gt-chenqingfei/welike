package com.redefine.welike.business.browse.ui.switcher;

import android.view.ViewGroup;

import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.BasePage;
import com.redefine.welike.business.browse.ui.page.BrowseDiscoverCPage;
import com.redefine.welike.business.browse.ui.page.BrowseHomePage;
import com.redefine.welike.business.browse.ui.viewmodel.BrowseMainViewModel;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;
import com.redefine.welike.commonui.event.expose.MainTabDisplayManager;
import com.redefine.welike.frameworkmvvm.BaseLifecyclePageStackManager;

public class BrowseFragmentPageSwitcher {

    private final BaseLifecyclePageStackManager mStackManager;
    private BrowseHomePage mHomeFragmentPage;
    private BrowseDiscoverCPage browseDiscoverCPage;

    private BasePage mLastShowFragment;
    private int mLastIndex = -1;

    public static final int FRAGMENT_HOME_POSITION = 0;
    public static final int FRAGMENT_DISCOVER_POSITION = 1;

    private BrowseMainViewModel mainViewModel;

    public BrowseFragmentPageSwitcher(BaseLifecyclePageStackManager fragmentManager) {
        mStackManager = fragmentManager;
    }


    public void setCurrentItem(ViewGroup viewGroup, int index) {
        if (mLastIndex == index) {
            return;
        }
        mLastIndex = index;
        BasePage fragment = findFragmentByIndex(index);
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
        MainTabDisplayManager.getInstance().switchTab(resolvePageName(mLastShowFragment));
    }


    private BasePage findFragmentByIndex(int index) {
        BasePage fragment;
        switch (index) {
            case FRAGMENT_HOME_POSITION:
                fragment = mHomeFragmentPage;
                break;
            case FRAGMENT_DISCOVER_POSITION:
                fragment = browseDiscoverCPage;
                break;
            default:
                fragment = mHomeFragmentPage;
                break;

        }
        return fragment;
    }


    private BasePage getFragmentByIndex(int index) {
        BasePage fragment;
        switch (index) {
            case FRAGMENT_HOME_POSITION:
                if (mHomeFragmentPage == null) {
                    mHomeFragmentPage = new BrowseHomePage(mStackManager, new PageConfig.Builder(BrowseHomePage.class).build(), null);
                    if (mainViewModel != null) mHomeFragmentPage.setMainViewModel(mainViewModel);
                }
                fragment = mHomeFragmentPage;

                break;
            case FRAGMENT_DISCOVER_POSITION:

                if (browseDiscoverCPage == null) {
                    browseDiscoverCPage = new BrowseDiscoverCPage(mStackManager, new PageConfig.Builder(BrowseDiscoverCPage.class).build(), null);
                }
                fragment = browseDiscoverCPage;
                break;
            default:

                if (mHomeFragmentPage == null) {
                    mHomeFragmentPage = new BrowseHomePage(mStackManager, new PageConfig.Builder(BrowseHomePage.class).build(), null);
                }
                fragment = mHomeFragmentPage;

                break;

        }
        return fragment;
    }


    public void setMainViewModel(BrowseMainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }

    public void destroy(ViewGroup viewGroup) {
        if (mHomeFragmentPage != null) {
            mHomeFragmentPage.destroy(viewGroup);
        }
        if (browseDiscoverCPage != null) {
            browseDiscoverCPage.destroy(viewGroup);
        }

    }

    public void onPageStateChanged(int oldPageState, int pageState) {
        if (mLastShowFragment != null) {
            mLastShowFragment.onPageStateChanged(oldPageState, pageState);
        }
    }


    public void onActivityPause() {
        if (mHomeFragmentPage != null) {
            mHomeFragmentPage.onActivityPause();
        }
        if (browseDiscoverCPage != null) {
            browseDiscoverCPage.onActivityPause();
        }
    }

    public void onActivityResume() {
        if (mHomeFragmentPage != null) {
            mHomeFragmentPage.onActivityResume();
        }
        if (browseDiscoverCPage != null) {
            browseDiscoverCPage.onActivityResume();
        }
    }


    public void refreshTab(int index) {
        if (index == FRAGMENT_HOME_POSITION) {
            if (mHomeFragmentPage != null) {
                mHomeFragmentPage.refresh();
            }

        } else if (index == FRAGMENT_DISCOVER_POSITION) {
            if (browseDiscoverCPage != null) {
                browseDiscoverCPage.refresh();
            }
        }
    }
    private String resolvePageName(BasePage fragment) {
        if (fragment instanceof BrowseHomePage) {
            return FeedConstant.TAB_NAME_HOME;
        } else if (fragment instanceof BrowseDiscoverCPage) {
            return FeedConstant.TAB_NAME_DISCOVER;
        } else {
            return FeedConstant.TAB_NAME_HOME;
        }
    }

}
