package com.redefine.welike.business.feeds.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.redefine.commonui.loadmore.adapter.HomeEndLessRecylerOnScrollerListener;
import com.redefine.commonui.loadmore.adapter.IScrollDelegate;
import com.redefine.commonui.util.ViewUtil;
import com.redefine.commonui.widget.EmptyView;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.utils.LogUtil;
import com.redefine.multimedia.player.apollo.ApolloVideoView;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter;
import com.redefine.welike.business.feeds.ui.contract.IMainHomeContract;
import com.redefine.welike.commonui.event.expose.ItemExposeManager;
import com.redefine.welike.common.FragmentVisibleDetector;
import com.redefine.welike.commonui.view.ClassicsHeader1;
import com.redefine.welike.commonui.view.VideoItemVisibilityCalculator;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.manager.PostEventManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;


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

public class MainHomeView implements IMainHomeContract.IMainHomeView, IScrollDelegate, ErrorView.IErrorViewClickListener {
    /**
     * maybe recycle begin
     **/
    private View mRootView;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private EmptyView mEmptyView;
    private ErrorView mErrorView;
    private LoadingView mLoadingView;
    private TextView mMainTitleView;
    private ItemExposeManager mPostViewTimeManager;
//    View fixBackground;
//    TextView fixText;
//    LottieAnimationView fixAnim;
    /**
     * maybe recycle end
     **/
    private IMainHomeContract.IMainHomePresenter mPresenter;
    private ClassicsHeader1 mClassicsHeader;

    private final FragmentVisibleDetector mFragmentVisibleDetector = new FragmentVisibleDetector(new FragmentVisibleDetector.AutoPlayVideoListener() {
        @Override
        public void autoPlay() {
            if (mRecyclerView == null || mVideoItemVisibilityCalculator == null) {
                return;
            }
            mVideoItemVisibilityCalculator.onScrollStateChanged(mRecyclerView, RecyclerView.SCROLL_STATE_IDLE);
        }

        @Override
        public void stopPlay() {
            if (mRecyclerView == null || mVideoItemVisibilityCalculator == null) {
                return;
            }
            RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
            if (adapter != null && adapter instanceof FeedRecyclerViewAdapter) {
                ((FeedRecyclerViewAdapter) adapter).destroyVideo();
                mVideoItemVisibilityCalculator.resetCurrentPlayItem();
            }
        }
    });

    private VideoItemVisibilityCalculator mVideoItemVisibilityCalculator = new VideoItemVisibilityCalculator(new VideoItemVisibilityCalculator.VideoPlayStateCallback() {
        @Override
        public void stopPlayback() {
            RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
            if (adapter != null && adapter instanceof FeedRecyclerViewAdapter) {
                ((FeedRecyclerViewAdapter) adapter).destroyVideo();
                mVideoItemVisibilityCalculator.resetCurrentPlayItem();
            }
        }

        @Override
        public void startNewPlay(int position, ApolloVideoView videoPlayerView) {
            RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
            if (adapter != null && adapter instanceof FeedRecyclerViewAdapter) {
                ((FeedRecyclerViewAdapter) adapter).playVideo(position, videoPlayerView);
            }
        }
    });


    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.main_home_fragment, container, false);
        initViews();
        return mRootView;
    }

    private void initViews() {
        mMainTitleView = mRootView.findViewById(R.id.common_title_view);
        mMainTitleView.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "main_tab_home"));
        mRefreshLayout = mRootView.findViewById(R.id.main_home_refresh_layout);
        mRecyclerView = mRootView.findViewById(R.id.main_home_recycler_view);
        mEmptyView = mRootView.findViewById(R.id.common_empty_view);
        mErrorView = mRootView.findViewById(R.id.common_error_view);
        mLoadingView = mRootView.findViewById(R.id.common_loading_view);
        mClassicsHeader = mRootView.findViewById(R.id.main_home_refresh_header);
        mErrorView.setOnErrorViewClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mRootView.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnScrollListener(new HomeEndLessRecylerOnScrollerListener(this));
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setEnableOverScrollBounce(false);
        mEmptyView.showEmptyBtn(R.drawable.ic_common_empty, ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "main_tab_discover"), new EmptyView.IEmptyBtnClickListener() {
            @Override
            public void onClickEmptyBtn() {
                EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_HOME_TO_FORYOU, null));
            }
        });
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mPresenter.onRefresh();
                PostEventManager.INSTANCE.setAction(EventConstants.PULL_DOWN_REFRESH);
            }
        });
//        mRecyclerView.addOnScrollListener(mVideoItemVisibilityCalculator);
        mRecyclerView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                mFragmentVisibleDetector.attach();
                if (mPostViewTimeManager != null) {
                    mPostViewTimeManager.onAttach();
                }
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                mFragmentVisibleDetector.detach();
                if (mPostViewTimeManager != null) {
                    mPostViewTimeManager.onDetach();
                }
            }
        });
    }
//        fixBackground = mRootView.findViewById(R.id.home_notify_bg);
//        fixText = mRootView.findViewById(R.id.home_notify_tv);
//        fixAnim = mRootView.findViewById(R.id.home_notify_anim);
//        fixBackground.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                EventLog.InterestGuide.report2(AccountManager.getInstance().getAccount().getUid());
//                UserInterestSelectPage.launch(1);
//                setNotifyFix(false);
//            }
//        });
//        setNotifyFix(false);

//    public void setNotifyFix(boolean show) {
//        if (show) {
//            fixBackground.setVisibility(View.VISIBLE);
//            fixText.setVisibility(View.VISIBLE);
//            fixAnim.setVisibility(View.VISIBLE);
//            fixText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "fix_info"));
//            fixAnim.setAnimation("notify_fix_info.json");
//            fixAnim.playAnimation();
//            EventLog.InterestGuide.report1(AccountManager.getInstance().getAccount().getUid());
//        } else {
//            fixBackground.setVisibility(View.GONE);
//            fixText.setVisibility(View.GONE);
//            fixAnim.cancelAnimation();
//            fixAnim.setVisibility(View.GONE);
//        }
//    }

    @Override
    public void setPresenter(IMainHomeContract.IMainHomePresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setAdapter(RecyclerView.Adapter mAdapter) {
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void setRecyclerViewManager(ItemExposeManager manager) {
        mPostViewTimeManager = manager;
        manager.attach(mRecyclerView, (FeedRecyclerViewAdapter) mRecyclerView.getAdapter(), EventConstants.FEED_SOURCE_HOME);
    }

    @Override
    public Context getContext() {
        if (mRecyclerView == null) {
            return null;
        }
        return mRecyclerView.getContext();
    }

    @Override
    public void finishRefresh(boolean isSuccess) {
        mRefreshLayout.finishRefresh(300, isSuccess);
    }

    @Override
    public void autoRefresh() {
        mRefreshLayout.autoRefresh();
    }

    @Override
    public void setRefreshEnable(boolean b) {
        mRefreshLayout.setEnableRefresh(b);
    }

    @Override
    public void setRefreshCount(int size) {
//        mClassicsHeader.setRefreshCount(size);
    }

    @Override
    public void autoPlayVideo() {
        LogUtil.d("wng_", "MainHomeView autoPlayVideo()");
//        mRecyclerView.post(new Runnable() {
//            @Override
//            public void run() {
//                mVideoItemVisibilityCalculator.onScrollStateChanged(mRecyclerView, RecyclerView.SCROLL_STATE_IDLE);
//            }
//        });
    }

    @Override
    public void onActivityResume() {
        mFragmentVisibleDetector.onResume();
    }

    @Override
    public void onActivityPause() {
        mFragmentVisibleDetector.onPause();
    }

    @Override
    public void onFragmentShow() {
        mFragmentVisibleDetector.show();
    }

    @Override
    public void onFragmentHide() {
        mFragmentVisibleDetector.hide();
    }

    @Override
    public void destroy() {
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (adapter != null && adapter instanceof FeedRecyclerViewAdapter) {
            ((FeedRecyclerViewAdapter) adapter).destroy();
            mVideoItemVisibilityCalculator.resetCurrentPlayItem();
        }
    }

    @Override
    public void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
        mRefreshLayout.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showErrorView() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mRefreshLayout.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showEmptyView() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mRefreshLayout.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showContentView() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mRefreshLayout.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void scrollToTop() {
        mRecyclerView.scrollToPosition(0);
    }

    @Override
    public boolean canScroll() {
        return ViewUtil.canScroll(mRecyclerView);
    }

//    TourGuide tourGuide;

//    View guideTaregt;

    @Override
    public void showGuide(final String guide, final View view) {
//        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
//            @Override
//            public void run() {
//                if (mRootView.getContext() instanceof Activity) {
//                    tourGuide = GuideManager.INSTANCE.show(GuideManager.HOME_SUPER_LIKE, (Activity) mRootView.getContext());
//                    tourGuide.playOn(view);
//                }
//            }
//        });
    }

    @Override
    public void hideGuide(boolean click) {
//        if (tourGuide != null) {
//            if (click){
//                EventLog.Guide.report(GuideManager.HOME_SUPER_LIKE);
//            }
//            tourGuide.cleanUp();
//            tourGuide = null;
//        }
    }

    @Override
    public void onHeaderChange() {
//        if (tourGuide != null) {
//            tourGuide.cleanUp();
//            tourGuide = null;
//        }
    }

//    @Override
//    public void checkFixInfo() {
//        FixInfoManager.INSTANCE.check(new Function1<Boolean, Unit>() {
//            @Override
//            public Unit invoke(Boolean v) {
//                if (v) {
//                    setNotifyFix(true);
//                }
//                return null;
//            }
//        });
//    }
//
//    @Override
//    public void hideFixInfo() {
//        setNotifyFix(false);
//    }

    @Override
    public boolean canLoadMore() {
        return mPresenter.canLoadMore();
    }

    @Override
    public void onLoadMore() {
        mPresenter.onLoadMore();
        PostEventManager.INSTANCE.setAction(EventConstants.PULL_UP_REFRESH);
    }

    @Override
    public void onErrorViewClick() {
        mPresenter.onRefresh();
        PostEventManager.INSTANCE.setAction(EventConstants.CLICK_BUTTON_REFRESH);
    }

    @Override
    public void onSrollFirst() {
        mPresenter.hideRefresh();
    }

    @Override
    public void onSrollShowRefresh() {
        mPresenter.showRefresh();
    }
}
