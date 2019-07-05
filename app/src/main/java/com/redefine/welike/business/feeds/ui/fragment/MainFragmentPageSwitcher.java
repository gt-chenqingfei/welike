package com.redefine.welike.business.feeds.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.ViewGroup;

import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.BasePage;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.business.feeds.discovery.ui.page.DiscoveryLatestPage;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;
import com.redefine.welike.business.feeds.ui.page.HomePage;
import com.redefine.welike.commonui.event.expose.MainTabDisplayManager;
import com.redefine.welike.commonui.framework.PageStackManager;
import com.redefine.welike.frameworkmvvm.BaseLifecyclePageStackManager;


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

public class MainFragmentPageSwitcher {
    private final BaseLifecyclePageStackManager mStackManager;
    //    private HomeFragmentPage mHomeFragmentPage;
    private HomePage mHomeFragmentPage;
    //    private DiscoveryPage mDiscoverFragmentPage;
    private DiscoveryLatestPage mDiscoveryLatestPage;
    private MessageFragmentPage mMessageFragmentPage;
    private MineFragmentPage mMineFragmentPage;
    //    private HomeRecommendFragmentPage mHomeRecommendFragmentPage;
    private boolean hasFollowOld;

    private BasePage mLastShowFragment;
    private int mLastIndex = -1;

    public static final int FRAGMENT_HOME_POSITION = 0;
    public static final int FRAGMENT_DISCOVER_POSITION = 1;
    public static final int FRAGMENT_MESSAGE_POSITION = 2;
    public static final int FRAGMENT_MINE_POSITION = 3;

    public MainFragmentPageSwitcher(Activity activity) {
        mStackManager = new PageStackManager(activity);
    }

    public void showHomeForYou(boolean refresh) {

        if (mLastIndex == FRAGMENT_HOME_POSITION) {
            mHomeFragmentPage.showForYou();
            if (refresh) {
                mHomeFragmentPage.refresh();
            }
        }

    }

    public void setCurrentItem(ViewGroup viewGroup, int index, Bundle bundle) {
        if (mLastIndex == index) {
            return;
        }
        mLastIndex = index;
        BasePage fragment = findFragmentByIndex(index, bundle);
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
            fragment = getFragmentByIndex(index, bundle);
            fragment.createAndAttach(viewGroup);
        }
        MainTabDisplayManager.getInstance().switchTab(resolvePageName(fragment));
        mLastShowFragment = fragment;
    }


    private BasePage findFragmentByIndex(int index, Bundle bundle) {
        BasePage fragment;
        Account account = AccountManager.getInstance().getAccount();
        switch (index) {
            case FRAGMENT_HOME_POSITION:

//                    fragment = mHomeRecommendFragmentPage;
//                } else {
                boolean hasFollow;
                if ((account == null ? 0 : account.getFollowUsersCount()) <= 1) {
                    hasFollow = false;
                } else hasFollow = true;

                if (hasFollow && !hasFollowOld) {
                    hasFollowOld = true;
                    mHomeFragmentPage = new HomePage(mStackManager, new PageConfig.Builder(HomePage.class).setPageBundle(bundle).build(), null);
                }
                fragment = mHomeFragmentPage;
//                }
                break;
            case FRAGMENT_DISCOVER_POSITION:

//                fragment = mDiscoverFragmentPage;
                fragment = mDiscoveryLatestPage;
                break;
            case FRAGMENT_MESSAGE_POSITION:
                fragment = mMessageFragmentPage;
                break;
            case FRAGMENT_MINE_POSITION:
                fragment = mMineFragmentPage;
                break;
            default:
//                if (account != null && account.getFollowUsersCount() <= 1) {
//                    fragment = mHomeRecommendFragmentPage;
//                } else {
                fragment = mHomeFragmentPage;
//                }
                break;

        }
        return fragment;
    }


    private BasePage getFragmentByIndex(int index, Bundle bundle) {
        BasePage fragment;
        Account account = AccountManager.getInstance().getAccount();
        switch (index) {
            case FRAGMENT_HOME_POSITION:
//                if (account != null && account.getFollowUsersCount() <= 1) {
//                    if (mHomeRecommendFragmentPage == null) {
//                        mHomeRecommendFragmentPage = new HomeRecommendFragmentPage(mStackManager, new PageConfig.Builder(HomeRecommendFragmentPage.class).build(), null);
//                    }
//                    fragment = mHomeRecommendFragmentPage;
//                } else {
                if ((account == null ? 0 : account.getFollowUsersCount()) <= 1) {
                    hasFollowOld = false;
                } else hasFollowOld = true;

                if (mHomeFragmentPage == null) {
                    mHomeFragmentPage = new HomePage(mStackManager, new PageConfig.Builder(HomePage.class).setPageBundle(bundle).build(), null);
                }
                fragment = mHomeFragmentPage;
//                }

                break;
            case FRAGMENT_DISCOVER_POSITION:
//                if (mDiscoverFragmentPage == null) {
//                    mDiscoverFragmentPage = new DiscoveryPage(mStackManager, new PageConfig.Builder(DiscoveryPage.class).build(), null);
//                }
//                fragment = mDiscoverFragmentPage;
                if (mDiscoveryLatestPage == null) {
                    mDiscoveryLatestPage = new DiscoveryLatestPage(mStackManager, new PageConfig.Builder(DiscoveryLatestPage.class).setPageBundle(bundle).build(), null);
                }
                fragment = mDiscoveryLatestPage;
                break;
            case FRAGMENT_MESSAGE_POSITION:
                if (mMessageFragmentPage == null) {
                    mMessageFragmentPage = new MessageFragmentPage(mStackManager, new PageConfig.Builder(MessageFragmentPage.class).setPageBundle(bundle).build(), null);
                }
                fragment = mMessageFragmentPage;
                break;
            case FRAGMENT_MINE_POSITION:
                if (mMineFragmentPage == null) {
                    mMineFragmentPage = new MineFragmentPage(mStackManager, new PageConfig.Builder(MineFragmentPage.class).setPageBundle(bundle).build(), null);
                }
                fragment = mMineFragmentPage;
                break;
            default:

//                if (account != null && account.getFollowUsersCount() <= 1) {
//                    if (mHomeRecommendFragmentPage == null) {
//                        mHomeRecommendFragmentPage = new HomeRecommendFragmentPage(mStackManager, new PageConfig.Builder(HomeRecommendFragmentPage.class).build(), null);
//                    }
//                    fragment = mHomeRecommendFragmentPage;
//                } else {
                if (mHomeFragmentPage == null) {
                    mHomeFragmentPage = new HomePage(mStackManager, new PageConfig.Builder(HomePage.class).setPageBundle(bundle).build(), null);
                }
                fragment = mHomeFragmentPage;
//                }


                break;

        }
        return fragment;
    }

    public void destroy(ViewGroup viewGroup) {
        if (mHomeFragmentPage != null) {
            mHomeFragmentPage.destroy(viewGroup);
        }
//        if (mDiscoverFragmentPage != null) {
//            mDiscoverFragmentPage.destroy(viewGroup);
//        }
        if (mDiscoveryLatestPage != null) {
            mDiscoveryLatestPage.destroy(viewGroup);
        }

        if (mMessageFragmentPage != null) {
            mMessageFragmentPage.destroy(viewGroup);
        }
        if (mMineFragmentPage != null) {
            mMineFragmentPage.destroy(viewGroup);
        }
//        if (mHomeRecommendFragmentPage != null) {
//            mHomeRecommendFragmentPage.destroy(viewGroup);
//        }
    }

    public void onPageStateChanged(int oldPageState, int pageState) {
        if (mLastShowFragment != null) {
            // mLastShowFragment.onBasePageStateChanged(oldPageState, pageState);
            mLastShowFragment.onPageStateChanged(oldPageState, pageState);
        }
    }

    public void refreshHomeFollowing() {
        if (mHomeFragmentPage != null) {
            mHomeFragmentPage.showFollowAndRefresh();
        }
    }

    public void showHomeFolowing() {
        if (mHomeFragmentPage != null) {
            mHomeFragmentPage.showFollow();
        }
    }

    public void refreshTab(int index) {
        if (index == FRAGMENT_HOME_POSITION) {
//            Account account = AccountManager.getInstance().getAccount();
//            if (account != null && account.getFollowUsersCount() <= 1) {
//            } else {
            if (mHomeFragmentPage != null) {
                mHomeFragmentPage.refresh();
            }
//            }

        } else if (index == FRAGMENT_DISCOVER_POSITION) {
//            mDiscoverFragmentPage.refresh();
            mDiscoveryLatestPage.refresh();
        }
    }

    public void refreshFollowIfNeed() {
        if (mHomeFragmentPage != null) {
            mHomeFragmentPage.refreshIfNeed();
        }
    }

    public void refreshUserTask(Message message) {
        if (mLastShowFragment == mMineFragmentPage) {
            mMineFragmentPage.onNewMessage(message);
        } else if (mLastShowFragment == mHomeFragmentPage) {
            mHomeFragmentPage.onNewMessage(message);
        }
    }

    public void onActivityPause() {
        if (mHomeFragmentPage != null) {
            mHomeFragmentPage.onActivityPause();
        }
        if (mDiscoveryLatestPage != null) {
            mDiscoveryLatestPage.onActivityPause();
        }

        if (mMessageFragmentPage != null) {
            mMessageFragmentPage.onActivityPause();
        }
        if (mMineFragmentPage != null) {
            mMineFragmentPage.onActivityPause();
        }
//        if (mHomeRecommendFragmentPage != null) {
//            mHomeRecommendFragmentPage.onActivityPause();
//        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (mMineFragmentPage != null) {
            mMineFragmentPage.onActivityResult(requestCode, resultCode, data);
        }

    }

    public void onActivityResume() {
        if (mHomeFragmentPage != null) {
            mHomeFragmentPage.onActivityResume();
        }
        if (mDiscoveryLatestPage != null) {
            mDiscoveryLatestPage.onActivityResume();
        }

        if (mMessageFragmentPage != null) {
            mMessageFragmentPage.onActivityResume();
        }
        if (mMineFragmentPage != null) {
            mMineFragmentPage.onActivityResume();
        }
//        if (mHomeRecommendFragmentPage != null) {
//            mHomeRecommendFragmentPage.onActivityResume();
//        }

    }

    private String resolvePageName(BasePage fragment) {
        if (fragment instanceof HomeFragmentPage) {
            return FeedConstant.TAB_NAME_HOME;
        } else if (fragment instanceof DiscoveryLatestPage) {
            return FeedConstant.TAB_NAME_DISCOVER;
        } else if (fragment instanceof MessageFragmentPage) {
            return FeedConstant.TAB_NAME_MESSAGE;
        } else if (fragment instanceof MineFragmentPage) {
            return FeedConstant.TAB_NAME_ME;
//        } else if (fragment instanceof HomeRecommendFragmentPage) {
//            return FeedConstant.TAB_NAME_HOME_RECOMMEND;
        } else {
            return FeedConstant.TAB_NAME_HOME;
        }
    }

}
