package com.redefine.welike.business.feeds.ui.page;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidkun.xtablayout.XTabLayout;
import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.PageName;
import com.redefine.foundation.framework.Event;
import com.redefine.welike.R;
import com.redefine.welike.base.LanguageSupportManager;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.browse.ui.adapter.DiscoverPageAdapter;
import com.redefine.welike.business.feeds.discovery.ui.fragment.FeedFragment;
import com.redefine.welike.business.feeds.management.RecommendFollowManager;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;
import com.redefine.welike.business.feeds.ui.fragment.FollowFeedFragment;
import com.redefine.welike.business.feeds.ui.vm.HomeViewModel;
import com.redefine.welike.business.feeds.ui.widget.OnePlusFlipperView;
import com.redefine.welike.business.startup.management.constant.LanguageConstant;
import com.redefine.welike.business.startup.ui.dialog.LanguageChooseDialog;
import com.redefine.welike.business.user.management.FollowUserManager;
import com.redefine.welike.business.user.ui.fragment.HomeRecommendFragment;
import com.redefine.welike.common.abtest.ABKeys;
import com.redefine.welike.common.abtest.ABTest;
import com.redefine.welike.commonui.widget.SwitchViewPager;
import com.redefine.welike.frameworkmvvm.BaseLifecycleFragmentPage;
import com.redefine.welike.frameworkmvvm.BaseLifecyclePageStackManager;
import com.redefine.welike.frameworkmvvm.ViewModelProviders;
import com.redefine.welike.statistical.EventLog1;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author redefine honlin
 * @Date on 2018/10/25
 * @Description new version
 */

@PageName("HomeFragmentPage")
public class HomePage extends BaseLifecycleFragmentPage implements FollowUserManager.FollowUserCallback {


    private View rootView;
    private ConstraintLayout parentView;

    private ImageView ivChooseLanguage, ivAddFriends;

    private XTabLayout tabMain, tabInterest;

    private SwitchViewPager discoveryViewPager;
    private DiscoverPageAdapter browseDiscoveryPageAdapter;

    private List<Fragment> fragmentPages;
    private ArrayList<String> titles = new ArrayList<>();

    private int lastTrendingIndex = 1;//Hot 显示的index

    private int currentPosition = 0;

    private HomeViewModel homeViewModel;
    private OnePlusFlipperView badgeView;
    private boolean forceShowFollowing;

    public HomePage(BaseLifecyclePageStackManager stackManager, PageConfig config, PageCache cache) {
        super(stackManager, config, cache);
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        parseBundle(config.pageBundle);
    }

    @NonNull
    @Override
    protected View createView(ViewGroup container, Bundle saveState) {

        rootView = LayoutInflater.from(container.getContext()).inflate(R.layout.page_home_layout, null);
        return rootView;
    }

    @Override
    protected void initView(ViewGroup container, Bundle saveState) {
        super.initView(container, saveState);

        ivChooseLanguage = rootView.findViewById(R.id.iv_choose_language);
        ivAddFriends = rootView.findViewById(R.id.iv_add_friends);
        tabMain = rootView.findViewById(R.id.tab_main);
        tabInterest = rootView.findViewById(R.id.tab_interest);
        discoveryViewPager = rootView.findViewById(R.id.discovery_view_pager);
        badgeView = rootView.findViewById(R.id.badge_view);
        if (RecommendFollowManager.INSTANCE.shouldAlert()) {
            badgeView.setVisibility(View.VISIBLE);
        } else {
            badgeView.setVisibility(View.GONE);
        }

        ivChooseLanguage.setImageResource(LanguageConstant.getLanguageSrc(LanguageSupportManager.getInstance().getCurrentMenuLanguageType()));

        initViewPager();

        setEvent();

        setViewModel();

        homeViewModel.init();

    }

    private void parseBundle(Bundle pageBundle) {
        if (pageBundle != null) {
            forceShowFollowing = pageBundle.getBoolean(FeedConstant.BUNDLE_KEY_FORCE_SHOW_FOLLOWING);
        }
    }

    private void setEvent() {

        ivAddFriends.setImageResource(R.drawable.ic_add_contact_1);
        ivAddFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("from", EventLog1.AddFriend.ButtonFrom.HOME);
                EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_CONTACT, bundle));
            }
        });

        discoveryViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position >= 1) {
                    tabInterest.setVisibility(View.VISIBLE);
                    lastTrendingIndex = position;
                    if (tabMain.getSelectedTabPosition() == 1) return;
                    tabMain.getTabAt(1).select();
                } else {
                    tabInterest.setVisibility(View.GONE);
                    tabMain.getTabAt(0).select();
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        tabInterest.setOnTabSelectedListener(new XTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(XTabLayout.Tab tab) {
                discoveryViewPager.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(XTabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(XTabLayout.Tab tab) {
                // TODO: 2018/10/25 ?????

                refresh();

            }
        });


        ivChooseLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LanguageChooseDialog.show(v.getContext());
//                RecommendFollowActivity.launch();

            }
        });


        tabMain.setOnTabSelectedListener(new XTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(XTabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    showFollow();
                } else {
                    showTrending();
                }
            }

            @Override
            public void onTabUnselected(XTabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(XTabLayout.Tab tab) {
            }
        });


    }

    @Override
    public Bundle onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    private void setViewModel() {

        homeViewModel.getInterests().observe(this, new Observer<List<UserBase.Intrest>>() {
            @Override
            public void onChanged(@Nullable List<UserBase.Intrest> interests) {
                initInterests(interests);
            }
        });
    }

    private void initInterests(List<UserBase.Intrest> interests) {
        if (interests == null) return;
        {
            List<FeedFragment> fragmentPages = new ArrayList<>();
            for (UserBase.Intrest interest : interests) {
                FeedFragment fragment = FeedFragment.create(interest.getIid(), mPageStackManager);
                fragmentPages.add(fragment);
                titles.add(interest.getLabel());
            }
            this.fragmentPages.addAll(fragmentPages);
            browseDiscoveryPageAdapter.setTitles(titles);
            browseDiscoveryPageAdapter.insertFragments(fragmentPages);

        }
    }

    private void initViewPager() {

        fragmentPages = new ArrayList<>();

        Activity activity = mPageStackManager.getActivity();

        FragmentManager fragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();

        browseDiscoveryPageAdapter = new DiscoverPageAdapter(fragmentManager);

        if (forceShowFollowing) {
            fragmentPages.add(new FollowFeedFragment());
        } else {
            Account account = AccountManager.getInstance().getAccount();
            if (account != null && account.getFollowUsersCount() <= 1) {

                if (ABTest.INSTANCE.check(ABKeys.TEST_RECOMMEND) == 0)
                    fragmentPages.add(new HomeRecommendFragment());
                else {
                    fragmentPages.add(HomeBigCardRecomFragment.Companion.create());
                }
            } else {
                fragmentPages.add(new FollowFeedFragment());
            }
        }

        fragmentPages.add(FeedFragment.create(FeedFragment.FOR_YOU, mPageStackManager));
        fragmentPages.add(FeedFragment.create(BrowseConstant.INTEREST_VIDEO, mPageStackManager));

        titles.add(ResourceTool.getString(""));
        titles.add(ResourceTool.getString("discover_For_You"));
        titles.add(ResourceTool.getString("discover_status_video"));

        browseDiscoveryPageAdapter.initFragments(fragmentPages);

        browseDiscoveryPageAdapter.setTitles(titles);

        discoveryViewPager.setAdapter(browseDiscoveryPageAdapter);

        tabInterest.setupWithViewPager(discoveryViewPager);

        tabMain.addTab(tabMain.newTab().setText(ResourceTool.getString("following_btn_text")));
        tabMain.addTab(tabMain.newTab().setText(ResourceTool.getString("discover_hot")));

        if (forceShowFollowing) {
            tabMain.getTabAt(0).select();
            showFollow();
        } else {
            tabMain.getTabAt(1).select();
            showTrending();
        }
        setBadgeViewPosition();
    }


    public void showFollowAndRefresh() {
        showFollow();
        refresh();
    }

    public void showFollow() {
        tabInterest.setVisibility(View.GONE);
        discoveryViewPager.setCurrentItem(0, false);
        currentPosition = 0;
    }


    private void showTrending() {
        tabInterest.setVisibility(View.VISIBLE);
        discoveryViewPager.setCurrentItem(lastTrendingIndex, false);
        currentPosition = 1;
    }

    private void setBadgeViewPosition() {
        XTabLayout.Tab tab = tabMain.getTabAt(0);
        if (tab != null) {
            final View tabView = tab.getTabView();
            tabView.post(new Runnable() {
                @Override
                public void run() {
                    int tabLayoutWidth = tabMain.getMeasuredWidth();
                    float x = tabView.getRight();
                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) badgeView.getLayoutParams();
                    float margin = tabLayoutWidth / 2 - x;
                    if (margin < 0) {
                        layoutParams.leftMargin = Math.abs((int) margin);
                    } else {
                        layoutParams.rightMargin = (int) margin;
                    }

                }
            });
        }
    }

    public void refresh() {
        homeViewModel.refresh();

        Fragment fragment = fragmentPages.get(currentPosition == 0 ? 0 : lastTrendingIndex);
        if (fragment == null) return;
        if (fragment instanceof FollowFeedFragment) {
            ((FollowFeedFragment) fragment).refresh();
        } else if (fragment instanceof FeedFragment) {
            ((FeedFragment) fragment).refresh();
        }

    }

    public void refreshIfNeed() {
        Fragment fragment = fragmentPages.get(currentPosition == 0 ? 0 : lastTrendingIndex);
        if (fragment instanceof FollowFeedFragment) {
            ((FollowFeedFragment) fragment).refresh();
        }
    }

    @Override
    public void attach(ViewGroup container) {
        super.attach(container);
        FollowUserManager.getInstance().register(this);
    }

    @Override
    public void detach(ViewGroup container) {
        super.detach(container);
        FollowUserManager.getInstance().unregister(this);
    }

    @Override
    public void onActivityResume() {
        super.onActivityResume();
        FollowUserManager.getInstance().register(this);
    }

    @Override
    public void onActivityPause() {
        super.onActivityPause();
        FollowUserManager.getInstance().unregister(this);
    }

    @Override
    public void onPageStateChanged(int oldPageState, int pageState) {
        super.onPageStateChanged(oldPageState, pageState);
        if (oldPageState == PAGE_STATE_SHOW && pageState != PAGE_STATE_SHOW) {
        }
        if (pageState == PAGE_STATE_SHOW) {
        }

    }

    @Override
    public void onBasePageStateChanged(int oldPageState, int pageState) {
        super.onBasePageStateChanged(oldPageState, pageState);
        if (pageState == PAGE_STATE_SHOW) {

        }

    }

    public void showForYou() {
        lastTrendingIndex = 1;
        showTrending();
    }

    @Override
    public void onFollowCompleted(String uid, int errCode) {
        if (RecommendFollowManager.INSTANCE.shouldAlert()) {
            badgeView.startFlipper();
//            RecommendFollowManager.INSTANCE.ringAndVibrate();
            RecommendFollowManager.INSTANCE.vibrate();
        }
    }

    @Override
    public void onUnfollowCompleted(String uid, int errCode) {

    }
}
