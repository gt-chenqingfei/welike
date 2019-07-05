package com.redefine.welike.business.browse.ui.page;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidkun.xtablayout.XTabLayout;
import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.BasePage;
import com.pekingese.pagestack.framework.page.PageName;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.language.LocalizationManager;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.base.LanguageSupportManager;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.browse.ui.adapter.DiscoverPageAdapter;
import com.redefine.welike.business.browse.ui.fragment.FeedHotPostsFragment;
import com.redefine.welike.business.browse.ui.viewmodel.BrowseHomeViewModel;
import com.redefine.welike.business.browse.ui.viewmodel.BrowseMainViewModel;
import com.redefine.welike.business.startup.management.HalfLoginManager;
import com.redefine.welike.business.startup.management.StartEventManager;
import com.redefine.welike.business.startup.management.constant.LanguageConstant;
import com.redefine.welike.business.startup.management.constant.RegisteredConstant;
import com.redefine.welike.business.startup.ui.dialog.LanguageChooseDialog;
import com.redefine.welike.common.BrowseManager;
import com.redefine.welike.common.BrowseSchemeManager;
import com.redefine.welike.frameworkmvvm.BaseLifecyclePageStackManager;
import com.redefine.welike.frameworkmvvm.ViewModelProviders;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.bean.RegisterAndLoginModel;
import com.redefine.welike.statistical.manager.PostEventManager;
import com.redefine.welike.statistical.manager.UnLoginEventManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by honglin on 2018/6/13.
 **/
@PageName("BrowseHomePage")
public class BrowseHomePage extends BaseBrowseLifecyclePage {

    private DiscoverPageAdapter browseDiscoveryPageAdapter;
    private ViewPager mViewPager;
    private XTabLayout mTabLayout;
    private TextView tvLogin;
    private ImageView ivSearch, ivChooseLanguage;
    private View loginView;


    private List<UserBase.Intrest> mInterest = new ArrayList<>();
    private List<FeedHotPostsFragment> fragmentPages;
    private ArrayList<String> titles = new ArrayList<>();

    private BrowseHomeViewModel mDiscoveryViewModel;

    private BrowseMainViewModel mainViewModel;

    private int mCurrentSelectTab = 0;


    public BrowseHomePage(BaseLifecyclePageStackManager stackManager, PageConfig config, PageCache cache) {
        super(stackManager, config, cache);
        mDiscoveryViewModel = ViewModelProviders.of(this).get(BrowseHomeViewModel.class);
    }


    public void setMainViewModel(BrowseMainViewModel browseMainViewModel) {
        mainViewModel = browseMainViewModel;
    }

    @Override
    protected View createPageView(ViewGroup container, Bundle saveState) {
        return mPageStackManager.getLayoutInflater().inflate(R.layout.page_avoid_home, null);
    }

    @Override
    protected void initView(ViewGroup container, Bundle saveState) {
        mViewPager = getView().findViewById(R.id.discovery_view_pager);
        mTabLayout = getView().findViewById(R.id.tl_interest_tab);
        tvLogin = getView().findViewById(R.id.tv_login);
        ivSearch = getView().findViewById(R.id.iv_search);
        loginView = getView().findViewById(R.id.ll_bottom_login);
        ivChooseLanguage = getView().findViewById(R.id.iv_choose_language);
        ivChooseLanguage.setImageResource(LanguageConstant.getLanguageSrc(LanguageSupportManager.getInstance().getCurrentMenuLanguageType()));


        tvLogin.setVisibility(View.GONE);
        ivChooseLanguage.setVisibility(View.VISIBLE);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentSelectTab = position;
                onInterestSelect();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mTabLayout.addOnTabSelectedListener(new XTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(XTabLayout.Tab tab) {
                mCurrentSelectTab = tab.getPosition();
                onInterestSelect();
            }

            @Override
            public void onTabUnselected(XTabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(XTabLayout.Tab tab) {
                refresh();


            }
        });

        ivSearch.setImageResource(R.drawable.ic_add_contact_1);
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventLog1.AddFriend.report5();
                BrowseSchemeManager.getInstance().clear();

//                doHalfLogin();

                HalfLoginManager.getInstancce().showLoginDialog(v.getContext(), new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.OTHER));

            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BrowseSchemeManager.getInstance().clear();
                EventLog.UnLogin.report15();
                doHalfLogin();
            }
        });

        ivChooseLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LanguageChooseDialog.show(v.getContext());
            }
        });


        if (!BrowseManager.isHomeTest()) {
            loginView.setVisibility(View.VISIBLE);
            loginView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BrowseSchemeManager.getInstance().clear();
                    EventLog.UnLogin.report15();
                    Bundle bundle = new Bundle();
                    if (HalfLoginManager.getInstancce().isExistAccount) {
                        bundle.putSerializable(RegisteredConstant.KEY_EVENT_MODEL, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.LOGIN_BUTTON));
                        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_LOGIN_PAGE, bundle));
                    } else {
                        bundle.putSerializable(RegisteredConstant.KEY_EVENT_MODEL, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.LOGIN_BUTTON));
                        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_SIGN_LOGIN_PAGE, bundle));
                    }
                }
            });
        }

        initViewPager();

        changeViewByData();

        mDiscoveryViewModel.refreshInterest();
        PostEventManager.INSTANCE.reset();
        PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_UNLOGIN_FOR_YOU);
    }

    private void doHalfLogin() {
        if (HalfLoginManager.getInstancce().isExistAccount) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(RegisteredConstant.KEY_EVENT_MODEL, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.LOGIN_BUTTON));
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_LOGIN_PAGE, bundle));
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable(RegisteredConstant.KEY_EVENT_MODEL, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.LOGIN_BUTTON));
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_SIGN_LOGIN_PAGE, bundle));
        }
    }


    private void initViewPager() {
        fragmentPages = new ArrayList<>();

        Activity activity = mPageStackManager.getActivity();

        FragmentManager fragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();

        browseDiscoveryPageAdapter = new DiscoverPageAdapter(fragmentManager);

        FeedHotPostsFragment feedHotPostsPage = FeedHotPostsFragment.create(BrowseConstant.INTEREST_ALL);
        feedHotPostsPage.setDiscoveryViewModel(mDiscoveryViewModel);
        fragmentPages.add(feedHotPostsPage);

        FeedHotPostsFragment feedVideoPostsPage = FeedHotPostsFragment.create(BrowseConstant.INTEREST_VIDEO);
        feedVideoPostsPage.setDiscoveryViewModel(mDiscoveryViewModel);
        fragmentPages.add(feedVideoPostsPage);

        titles.add(ResourceTool.getString("discover_For_You"));
        titles.add(ResourceTool.getString("discover_status_video"));

        browseDiscoveryPageAdapter.initFragments(fragmentPages);
        browseDiscoveryPageAdapter.setTitles(titles);
        mViewPager.setAdapter(browseDiscoveryPageAdapter);


        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void changeViewByData() {

        mDiscoveryViewModel.getIntrests().observe(this, new Observer<List<UserBase.Intrest>>() {
            @Override
            public void onChanged(@Nullable List<UserBase.Intrest> intrests) {
                if (intrests == null) return;
                initInterests(intrests);
            }
        });

        mDiscoveryViewModel.getShowSnakeOnMain().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean == null)
                    return;
                if (mainViewModel == null) return;
                mainViewModel.setSnakeBarShouldShow(aBoolean);
            }
        });
    }

    @Override
    public void attach(ViewGroup container) {
        super.attach(container);
        if (browseDiscoveryPageAdapter != null) browseDiscoveryPageAdapter.setFragmentVisible(true);
    }

    @Override
    public void detach(ViewGroup container) {
        super.detach(container);
        if (browseDiscoveryPageAdapter != null)
            browseDiscoveryPageAdapter.setFragmentVisible(false);
    }

    @Override
    public void onActivityStart() {
        super.onActivityStart();
    }

    @Override
    public void onActivityDestroy() {
        super.onActivityDestroy();
    }

    private void initInterests(List<UserBase.Intrest> intrests) {

        if (!CollectionUtil.isEmpty(intrests)) {
            ArrayList<Fragment> fragmentPages = new ArrayList<>();
            mInterest.clear();
            mInterest.addAll(intrests);

            FeedHotPostsFragment feedHotPostsPage;
            for (UserBase.Intrest intrest : intrests) {
                feedHotPostsPage = FeedHotPostsFragment.create(intrest.getIid());
                feedHotPostsPage.setDiscoveryViewModel(mDiscoveryViewModel);
                //页面第一次被创建的时候回调onPageStateChanged，此时这几个interest的Fragment还没创建出来，所以他们的onPageStateChanged不会被回调，
                //导致状态不正确，所以在这里手动调用一次，来矫正。
                feedHotPostsPage.onPageStateChanged(BasePage.PAGE_STATE_HIDE, BasePage.PAGE_STATE_SHOW);
                fragmentPages.add(feedHotPostsPage);
                BrowseHomePage.this.fragmentPages.add(feedHotPostsPage);
                titles.add(intrest.getLabel());
            }
            browseDiscoveryPageAdapter.setTitles(titles);
            browseDiscoveryPageAdapter.insertFragments(fragmentPages);
            browseDiscoveryPageAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onPageStateChanged(int oldPageState, int pageState) {
        if (!CollectionUtil.isEmpty(fragmentPages)) {
            for (FeedHotPostsFragment fragmentPage : fragmentPages) {
                fragmentPage.onPageStateChanged(oldPageState, pageState);
            }
        }
        if (pageState == BasePage.PAGE_STATE_SHOW) {
            EventLog.UnLogin.report7(LocalizationManager.getInstance().getCurrentLanguage(), StartEventManager.getInstance().getVidmate_page_source(),
                    UnLoginEventManager.INSTANCE.getFirstClickLanguage());
        }
    }

    private void onInterestSelect() {
        PostEventManager.INSTANCE.reset();
        if (CollectionUtil.isEmpty(mInterest)) {
            return;
        }
        if (mCurrentSelectTab == 0) {
            EventLog.UnLogin.report10(BrowseConstant.INTEREST_ALL);
            StartEventManager.getInstance().setInterestId(EventConstants.LABEL_FOR_YOU);
            PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_UNLOGIN_FOR_YOU);
        } else if (mCurrentSelectTab == 1) {
            EventLog.UnLogin.report10(BrowseConstant.INTEREST_VIDEO);
            StartEventManager.getInstance().setInterestId(EventConstants.LABEL_VIDEO);
            PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_UNLOGIN_VIDEO);
        } else {
            try {
                UserBase.Intrest intrest = mInterest.get(mCurrentSelectTab - 2);
                EventLog.UnLogin.report10(intrest.getIid());
                StartEventManager.getInstance().setInterestId(intrest.getIid());
                PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_UNLOGIN + intrest.getIid());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void refresh() {
        try {
//            discoverAppBar.setExpanded(true, true);
            if (fragmentPages.size() < mCurrentSelectTab) return;
            if (mCurrentSelectTab < 0) return;
            fragmentPages.get(mCurrentSelectTab).autoRefresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
