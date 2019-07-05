package com.redefine.welike.business.browse.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pekingese.pagestack.framework.page.BasePage;
import com.pekingese.pagestack.framework.page.PageName;
import com.redefine.commonui.enums.PageLoadMoreStatusEnum;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.commonui.loadmore.adapter.EndlessRecyclerOnScrollListener;
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate;
import com.redefine.commonui.loadmore.adapter.OnClickRetryListener;
import com.redefine.commonui.widget.EmptyView;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.multimedia.player.apollo.ApolloVideoView;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.browse.management.bean.Interest;
import com.redefine.welike.business.browse.management.bean.InterestPost;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.browse.ui.adapter.BrowseFeedRecyclerViewAdapter;
import com.redefine.welike.business.browse.ui.listener.IBrowseClickListener;
import com.redefine.welike.business.browse.ui.listener.InterestSelectListener;
import com.redefine.welike.business.browse.ui.viewmodel.BrowseDiscoverCViewModel;
import com.redefine.welike.business.browse.ui.viewmodel.BrowseHomeSubViewModel;
import com.redefine.welike.business.browse.ui.viewmodel.BrowseHomeViewModel;
import com.redefine.welike.business.common.mission.MissionManager;
import com.redefine.welike.business.common.mission.MissionType;
import com.redefine.welike.business.common.mission.OnPostButtonClickListener;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter;
import com.redefine.welike.commonui.event.expose.ItemExposeManager;
import com.redefine.welike.business.startup.management.StartEventManager;
import com.redefine.welike.common.PageVisibleDetector;
import com.redefine.welike.common.ScoreManager;
import com.redefine.welike.commonui.event.expose.impl.ItemExposeCallbackFactory;
import com.redefine.welike.commonui.util.ToastUtils;
import com.redefine.welike.commonui.view.VideoItemVisibilityCalculator;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.manager.GPScoreEventManager;
import com.redefine.welike.statistical.manager.InterestAndRecommendCardEventManager;
import com.redefine.welike.statistical.manager.PostEventManager;
import com.redefine.welike.statistical.manager.ShareEventManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by mengnan on 2018/6/14.
 **/
@PageName("FeedHotPostsPage")
public class FeedHotPostsFragment extends Fragment implements ILoadMoreDelegate, OnClickRetryListener, OnPostButtonClickListener, ErrorView.IErrorViewClickListener, IBrowseClickListener {
    private RecyclerView mRecyclerView;
    private BrowseFeedRecyclerViewAdapter mAdapter;

    private ErrorView mErrorView;
    private EmptyView mEmptyView;
    private View mLoadingView;
    private BrowseHomeSubViewModel mDiscoveryViewModel;
    private BrowseHomeViewModel baseViewModel;
//    private BrowseDiscoverCViewModel baseDiscoveryViewModel;

    private SmartRefreshLayout mRefreshLayout;

    private String interestId;
    private String pageSource;

    private ItemExposeManager mPostViewTimeManager = new ItemExposeManager(ItemExposeCallbackFactory.Companion.createPostExposeCallback(),
            ItemExposeCallbackFactory.Companion.createPostViewCallback(),
            ItemExposeCallbackFactory.Companion.createPostFollowBtnCallback());


    private final PageVisibleDetector pageVisibleDetector = new PageVisibleDetector(new PageVisibleDetector.AutoPlayVideoListener() {
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
            if (adapter instanceof FeedRecyclerViewAdapter) {
                ((FeedRecyclerViewAdapter) adapter).destroyVideo();
                mVideoItemVisibilityCalculator.resetCurrentPlayItem();

            }
        }
    });

    private final VideoItemVisibilityCalculator mVideoItemVisibilityCalculator = new VideoItemVisibilityCalculator(new VideoItemVisibilityCalculator.VideoPlayStateCallback() {
        @Override
        public void stopPlayback() {
            RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
            if (adapter instanceof FeedRecyclerViewAdapter) {
                ((FeedRecyclerViewAdapter) adapter).destroyVideo();
                mVideoItemVisibilityCalculator.resetCurrentPlayItem();

            }
        }

        @Override
        public void startNewPlay(int position, ApolloVideoView videoPlayerView) {
            RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
            if (adapter instanceof FeedRecyclerViewAdapter) {
                ((FeedRecyclerViewAdapter) adapter).playVideo(position, videoPlayerView);
            }
        }
    });


    public static FeedHotPostsFragment create(String interest) {
        Bundle bundle = new Bundle();
        bundle.putString("interest_id", interest);
        FeedHotPostsFragment fragment = new FeedHotPostsFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDiscoveryViewModel = ViewModelProviders.of(this).get(BrowseHomeSubViewModel.class);
    }

    public void autoRefresh() {
        if (mDiscoveryViewModel != null) {
            mRecyclerView.scrollToPosition(0);
            mRefreshLayout.autoRefresh();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            interestId = getArguments().getString("interest_id", BrowseConstant.INTEREST_ALL);
        }

        View view = inflater.inflate(R.layout.feed_posts_view_page, container, false);

        initView(view);

        return view;
    }

    public void setDiscoveryViewModel(BrowseHomeViewModel discoveryViewModel) {
        baseViewModel = discoveryViewModel;
    }

//    public void setDiscoveryViewModel(BrowseDiscoverCViewModel discoveryViewModel) {
//        baseDiscoveryViewModel = discoveryViewModel;
//        if (mRefreshLayout != null) mRefreshLayout.setEnableRefresh(false);
//    }


    public void scrollToTop() {
        if (null != mRecyclerView) {
            mRecyclerView.scrollToPosition(0);
        }
    }

    public void refresh() {
        if (mDiscoveryViewModel == null) return;
        mRecyclerView.scrollToPosition(0);
//        if (baseDiscoveryViewModel != null)
//            mDiscoveryViewModel.refresh(interestId, mAdapter.getFeedSource());
//        else
            mRefreshLayout.autoRefresh();
    }

    private void changeViewWithData() {
        mDiscoveryViewModel.getHotPosts().observe(this, new Observer<List<PostBase>>() {
            @Override
            public void onChanged(@Nullable List<PostBase> postBases) {
                if (TextUtils.equals(interestId, BrowseConstant.INTEREST_VIDEO)) return;
                mAdapter.addNewData(postBases);
                pageVisibleDetector.onDataLoaded();
                if (null != postBases && postBases.size() > 0) {
                    if (getUserVisibleHint()) {
                        ScoreManager.setRefreshCount(getContext());

                        if (ScoreManager.canShowScoreView(getContext())) {
                            GPScoreEventManager.INSTANCE.setAction_type(EventConstants.GP_ACTION_TYPE_REFRESH);
                            mAdapter.addScoreData(0);
                        }
                    }
                    showContentView();
                    mPostViewTimeManager.setData(postBases, mAdapter.hasHeader());
                } else {
                    showEmptyView();
                }
                mPostViewTimeManager.onDataLoaded();
            }
        });

        mDiscoveryViewModel.getVideoPosts().observe(this, new Observer<List<PostBase>>() {
            @Override
            public void onChanged(@Nullable List<PostBase> postBases) {

                if (!TextUtils.equals(interestId, BrowseConstant.INTEREST_VIDEO)) return;
                mAdapter.addNewData(postBases);

                pageVisibleDetector.onDataLoaded();
                if (null != postBases && postBases.size() > 0) {
                    showContentView();
                    mPostViewTimeManager.setData(postBases, mAdapter.hasHeader());
                } else {
                    showEmptyView();
                }
                mPostViewTimeManager.onDataLoaded();
                
            }
        });

        mDiscoveryViewModel.getmMorePosts().observe(this, new Observer<List<PostBase>>() {
            @Override
            public void onChanged(@Nullable List<PostBase> postBases) {

                mAdapter.addHisData(postBases);

                pageVisibleDetector.onDataLoaded();
                if (null != postBases && postBases.size() > 0) {
                    showContentView();
                    mPostViewTimeManager.setData(postBases, mAdapter.hasHeader());
                } else {
                    showEmptyView();
                }
                mPostViewTimeManager.onDataLoaded();
            }
        });


        mDiscoveryViewModel.getPageStatus().observe(this, new Observer<PageStatusEnum>() {
            @Override
            public void onChanged(@Nullable PageStatusEnum pageStatusEnum) {
                if (pageStatusEnum == null) {
                    return;
                }

                mRefreshLayout.finishRefresh();

                switch (pageStatusEnum) {
                    case EMPTY:
                        if (mAdapter.getRealItemCount() == 0) {
                            showEmptyView();
                        } else {
                            showContentView();
                        }
                        break;
                    case ERROR:
                        if (mAdapter.getRealItemCount() == 0) {
                            showErrorView();
                        } else {
                            showContentView();
                        }

                        break;
                    case CONTENT:
                        if (mAdapter.getRealItemCount() == 0) {
                            showEmptyView();
                        } else {
                            showContentView();
                        }

                        break;
                    case LOADING:
                        if (mAdapter.getRealItemCount() == 0) {
                            showLoading();
                        } else {
                            showContentView();
                        }

                        break;
                }
            }
        });
        mDiscoveryViewModel.getLoadMoreStatus().observe(this, new Observer<PageLoadMoreStatusEnum>() {
            @Override
            public void onChanged(@Nullable PageLoadMoreStatusEnum pageLoadMoreStatusEnum) {
                if (pageLoadMoreStatusEnum == null) {
                    return;
                }
                switch (pageLoadMoreStatusEnum) {
                    case NONE:
                        mAdapter.clearFinishFlag();
                        break;
                    case FINISH:
                        mAdapter.finishLoadMore();
                        break;
                    case LOADING:
                        mAdapter.onLoadMore();
                        break;
                    case NO_MORE:
                        mAdapter.noMore();
                        break;
                    case LOAD_ERROR:
                        mAdapter.loadError();
                        break;
                }
            }
        });

        mDiscoveryViewModel.getInterestPost().observe(this, new Observer<InterestPost>() {
            @Override
            public void onChanged(@Nullable InterestPost interestPost) {
                if (interestPost == null) return;
                InterestAndRecommendCardEventManager.INSTANCE.setFrom_page(EventConstants.INTEREST_CARD_FROM_PAGE_UNLOGIN);
                InterestAndRecommendCardEventManager.INSTANCE.report1();
                mAdapter.addInterestData(interestPost);

            }
        });

    }

    protected void initView(View view) {

        mRefreshLayout = view.findViewById(R.id.feed_refresh_layout);
        mErrorView = view.findViewById(R.id.discover_error_view);
        mLoadingView = view.findViewById(R.id.discover_loading_view);
        mEmptyView = view.findViewById(R.id.discover_empty_view);
        mErrorView.setOnErrorViewClickListener(this);
        mEmptyView.showEmptyBtn(R.drawable.ic_common_empty, ResourceTool.getString("feed_discover_empty_refresh"), new EmptyView.IEmptyBtnClickListener() {
            @Override
            public void onClickEmptyBtn() {

                if (baseViewModel != null) baseViewModel.refreshInterest();
                mDiscoveryViewModel.refresh(interestId, mAdapter.getFeedSource());
                PostEventManager.INSTANCE.setAction(EventConstants.CLICK_BUTTON_REFRESH);
            }
        });

        mRefreshLayout.setEnableOverScrollBounce(false);

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {

                if (baseViewModel != null) baseViewModel.refreshInterest();
                mDiscoveryViewModel.refresh(interestId, mAdapter.getFeedSource());
                PostEventManager.INSTANCE.setAction(EventConstants.PULL_DOWN_REFRESH);
                EventLog.UnLogin.report8(interestId);
            }
        });

        mErrorView.setClickable(false);
        mEmptyView.setClickable(false);
        mLoadingView.setClickable(false);

        mRecyclerView = view.findViewById(R.id.feed_posts_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(this));
//        mRecyclerView.addOnScrollListener(mVideoItemVisibilityCalculator);
        mRecyclerView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                pageVisibleDetector.attach();
                mPostViewTimeManager.onAttach();
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                pageVisibleDetector.detach();
                mPostViewTimeManager.onDetach();
            }
        });

        initAdapter();
        mPostViewTimeManager.attach(mRecyclerView, mAdapter, pageSource);
        changeViewWithData();
        mDiscoveryViewModel.refresh(interestId, mAdapter.getSource());
        PostEventManager.INSTANCE.setAction(EventConstants.AUTO_REFRESH);
    }

    private void initAdapter() {
        if (TextUtils.equals(interestId, BrowseConstant.INTEREST_ALL)) {
            mAdapter = new BrowseFeedRecyclerViewAdapter(EventConstants.FEED_PAGE_UNLOGIN_FOR_YOU);
            pageSource = EventConstants.FEED_SOURCE_UNLOGIN_FOR_YOU;
        } else if (TextUtils.equals(interestId, BrowseConstant.INTEREST_VIDEO)) {
            mAdapter = new BrowseFeedRecyclerViewAdapter(EventConstants.FEED_PAGE_UNLOGIN_VIDEO);
            pageSource = EventConstants.FEED_SOURCE_UNLOGIN_VIDEO;
        } else {
            mAdapter = new BrowseFeedRecyclerViewAdapter(EventConstants.FEED_PAGE_UNLOGIN + interestId);
            pageSource = EventConstants.FEED_SOURCE_UNLOGIN + interestId;
        }

        mAdapter.setBrowseClickListener(this);
        mAdapter.setRetryLoadMoreListener(this);
        mAdapter.setOnPostButtonClickListener(this);
        mAdapter.setInterestId(interestId);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setInterestSelectListener(new InterestSelectListener() {
            @Override
            public void onSelectInterest(@NotNull List<? extends Interest> list) {

                if (list.size() < 1) {
                    ToastUtils.showShort(ResourceTool.getString("user_interest_info_title"));
                    return;
                }
                mDiscoveryViewModel.insertInterest2DB(list);
            }
        });
    }

    @Override
    public boolean canLoadMore() {
        return mAdapter.canLoadMore();
    }

    @Override
    public void onLoadMore() {
        mDiscoveryViewModel.onHisLoad(interestId);
        mAdapter.onLoadMore();
        PostEventManager.INSTANCE.setAction(EventConstants.PULL_UP_REFRESH);
    }

    @Override
    public void onRetryLoadMore() {
        if (canLoadMore()) {
            onLoadMore();
        }
    }

    @Override
    public void onCommentClick(PostBase postBase) {
        MissionManager.INSTANCE.notifyEvent(MissionType.COMMENT);
        EventLog.Feed.report7(2, postBase == null ? "" : postBase.getPid(), PostEventManager.getPostType(postBase),
                postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
        EventLog.UnLogin.report23(EventConstants.UNLOGIN_FROM_PAGE_HOME);
    }

    @Override
    public void onLikeClick(PostBase postBase, int exp) {
        MissionManager.INSTANCE.notifyEvent(MissionType.LIKE);
        EventLog.Feed.report5(postBase == null ? "" : postBase.getPid(),
                postBase == null ? 0 : postBase.getSuperLikeExp(),
                exp, 2, PostEventManager.getPostType(postBase), postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
        EventLog.UnLogin.report22(EventConstants.UNLOGIN_FROM_PAGE_HOME, interestId);
    }

    @Override
    public void onForwardClick(PostBase postBase) {
        EventLog.Feed.report6(2, postBase == null ? null : postBase.getPid(), PostEventManager.getPostType(postBase),
                postBase == null ? null : postBase.getStrategy(), postBase == null ? null : postBase.getSequenceId());
    }

    @Override
    public void onFollowClick(PostBase postBase) {
        EventLog.UnLogin.report24(EventConstants.UNLOGIN_FROM_PAGE_HOME, postBase.getUid());
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            pageVisibleDetector.show();
            mPostViewTimeManager.onShow();
        } else {
            pageVisibleDetector.hide();
            mPostViewTimeManager.onHide();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        pageVisibleDetector.onResume();
        mPostViewTimeManager.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        pageVisibleDetector.onPause();
        mPostViewTimeManager.onPause();
    }

    public void onPageStateChanged(int oldPageState, int pageState) {
        if (pageState == BasePage.PAGE_STATE_SHOW) {
            pageVisibleDetector.pageShow();
        } else if (pageState == BasePage.PAGE_STATE_HIDE) {
            pageVisibleDetector.pageHide();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (adapter instanceof FeedRecyclerViewAdapter) {
            ((FeedRecyclerViewAdapter) adapter).destroy();
            mVideoItemVisibilityCalculator.resetCurrentPlayItem();
        }

        mPostViewTimeManager.onDestroy();
    }

    public void showLoading() {
//        if (baseDiscoveryViewModel != null)
//            baseDiscoveryViewModel.getPageStatus().postValue(PageStatusEnum.LOADING);
        mLoadingView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    public void showErrorView() {
//        if (baseDiscoveryViewModel != null)
//            baseDiscoveryViewModel.getPageStatus().postValue(PageStatusEnum.ERROR);
        mLoadingView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
    }

    public void showEmptyView() {
//        if (baseDiscoveryViewModel != null)
//            baseDiscoveryViewModel.getPageStatus().postValue(PageStatusEnum.EMPTY);
        mLoadingView.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    public void showContentView() {
//        if (baseDiscoveryViewModel != null)
//            baseDiscoveryViewModel.getPageStatus().postValue(PageStatusEnum.CONTENT);
        mLoadingView.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onErrorViewClick() {
        if (baseViewModel != null) baseViewModel.refreshInterest();
        PostEventManager.INSTANCE.setAction(EventConstants.CLICK_BUTTON_REFRESH);
        mDiscoveryViewModel.refresh(interestId, mAdapter.getFeedSource());
    }

    @Override
    public void onShareClick(PostBase postBase, int shareType) {
        ShareEventManager.INSTANCE.setFrompage(EventConstants.SHARE_FROM_PAGE_OTHER);
        ShareEventManager.INSTANCE.setShareType(shareType);
        ShareEventManager.INSTANCE.report3();
    }

    @Override
    public void onPostBodyClick(PostBase postBase) {
        ShareEventManager.INSTANCE.setChannel_page(EventConstants.SHARE_CHANNEL_PAGE_DISCOVER);
    }

    @Override
    public void onPostAreaClick(PostBase postBase, int clickArea) {

        if (postBase == null) return;

        EventLog.Feed.report8(pageSource, postBase.getUid(), PostEventManager.getPostRootUid(postBase),
                postBase.getPid(), PostEventManager.getPostRootPid(postBase), clickArea,
                PostEventManager.getPostType(postBase), postBase.getStrategy(), postBase.getSequenceId());
    }

    @Override
    public void onBrowseClick(int tye, boolean isShowLogin, int showType) {
        StartEventManager.getInstance().setActionType(tye > 5 ? 6 : tye);
        StartEventManager.getInstance().setFrom_page(1);
        if (tye == BrowseConstant.TYPE_SHARE) {
            EventLog.UnLogin.report16(StartEventManager.getInstance().getFrom_page(), interestId);
        }
    }

}
