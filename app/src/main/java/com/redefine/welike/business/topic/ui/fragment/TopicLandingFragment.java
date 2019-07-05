package com.redefine.welike.business.topic.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redefine.commonui.enums.PageLoadMoreStatusEnum;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.commonui.loadmore.adapter.EndlessRecyclerOnScrollListener;
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate;
import com.redefine.commonui.loadmore.adapter.OnClickRetryListener;
import com.redefine.commonui.util.ViewUtil;
import com.redefine.commonui.widget.BaseErrorView;
import com.redefine.commonui.widget.EmptyView;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.multimedia.player.apollo.ApolloVideoView;
import com.redefine.welike.R;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.browse.ui.listener.IBrowseClickListener;
import com.redefine.welike.business.common.mission.MissionManager;
import com.redefine.welike.business.common.mission.MissionType;
import com.redefine.welike.business.common.mission.OnPostButtonClickListener;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter;
import com.redefine.welike.commonui.event.expose.ItemExposeManager;
import com.redefine.welike.business.topic.ui.adapter.TopicLandingAdapter;
import com.redefine.welike.business.topic.ui.constant.TopicConstant;
import com.redefine.welike.business.topic.ui.contract.ITopicLandingContract;
import com.redefine.welike.business.topic.ui.page.TopicLandingActivity;
import com.redefine.welike.business.topic.ui.vm.TopicSubViewModel;
import com.redefine.welike.common.FragmentVisibleDetector;
import com.redefine.welike.commonui.event.expose.impl.ItemExposeCallbackFactory;
import com.redefine.welike.commonui.view.VideoItemVisibilityCalculator;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.manager.PostEventManager;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class TopicLandingFragment extends Fragment implements IBrowseClickListener {


    private EmptyView mEmptyView;
    private ErrorView mErrorView;
    private LoadingView mLoadingView;
    private RecyclerView mRecyclerView;

    private String mTopicId;

    private TopicLandingAdapter mAdapter;

    private int mCurrentSelectTab = TopicLandingActivity.TAB_HOT;
    private boolean isBrowse = false;

    private TopicSubViewModel topicSubViewModel;

    private ItemExposeManager mPostViewTimeManager = new ItemExposeManager(ItemExposeCallbackFactory.Companion.createPostExposeCallback(),
            ItemExposeCallbackFactory.Companion.createPostViewCallback(),
            ItemExposeCallbackFactory.Companion.createPostFollowBtnCallback());

    private ITopicLandingContract.ITopicLandingView iTopicLandingView;

    private final FragmentVisibleDetector fragmentVisibleDetector = new FragmentVisibleDetector(new FragmentVisibleDetector.AutoPlayVideoListener() {
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

    private VideoItemVisibilityCalculator mVideoItemVisibilityCalculator = new VideoItemVisibilityCalculator(new VideoItemVisibilityCalculator.VideoPlayStateCallback() {
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

    public static TopicLandingFragment create(String mTopicId, int mTab) {
        Bundle bundle = new Bundle();
        bundle.putString(TopicConstant.BUNDLE_KEY_TOPIC_ID, mTopicId);
        bundle.putInt(TopicConstant.BUNDLE_KEY_TOPIC_TAB, mTab);
        TopicLandingFragment fragment = new TopicLandingFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    public void setPresenter(ITopicLandingContract.ITopicLandingView iTopicLandingView) {
        this.iTopicLandingView = iTopicLandingView;
    }

    public void setCurrentSelectTab(int mCurrentSelectTab) {
        this.mCurrentSelectTab = mCurrentSelectTab;
        refresh();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        topicSubViewModel = ViewModelProviders.of(this).get(TopicSubViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_topic_landing_layout, null);


        initData();

        initViews(view);

        setAdapter();

        setEvent();

        setViewModel();

        PostEventManager.INSTANCE.setAction(EventConstants.AUTO_REFRESH);
        refresh();

        return view;


    }

    private void setViewModel() {
        topicSubViewModel.init(mTopicId, isBrowse);

        topicSubViewModel.getHotPosts().observe(this, new Observer<List<PostBase>>() {
            @Override
            public void onChanged(@Nullable List<PostBase> postBases) {
                if (!CollectionUtil.isEmpty(postBases) && mCurrentSelectTab == TopicLandingActivity.TAB_HOT) {
                    if (iTopicLandingView != null)
                        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                            @Override
                            public void run() {
                                iTopicLandingView.showChangeTab();
                            }
                        });
                }
                if (CollectionUtil.isEmpty(postBases) && mAdapter.getRealItemCount() == 0 && mCurrentSelectTab == TopicLandingActivity.TAB_HOT) {
                    if (iTopicLandingView != null) iTopicLandingView.hideChangeTab();
                    return;
                }

                if (CollectionUtil.isEmpty(postBases) && mAdapter.getRealItemCount() == 0 && mCurrentSelectTab == TopicLandingActivity.TAB_LATEST) {
                    if (iTopicLandingView != null) iTopicLandingView.showEmptyView();
                    showEmptyView();
                    return;
                }

                mAdapter.addNewData(postBases);

                mPostViewTimeManager.setData(postBases, mAdapter.hasHeader());

                mPostViewTimeManager.onDataLoaded();

                autoPlayVideo();

                PostEventManager.INSTANCE.setOldType(mCurrentSelectTab == TopicLandingActivity.TAB_HOT ? EventConstants.FEED_PAGE_OLD_TOPIC_HOT : EventConstants.FEED_PAGE_OLD_TOPIC_LATEST);
                PostEventManager.INSTANCE.setType(mAdapter.getFeedSource());
                PostEventManager.INSTANCE.sendEventStrategy(postBases);
            }
        });


        topicSubViewModel.getMorePosts().observe(this, new Observer<List<PostBase>>() {
            @Override
            public void onChanged(@Nullable List<PostBase> postBases) {
                mAdapter.addHisData(postBases);

                mPostViewTimeManager.updateData(postBases);

                mPostViewTimeManager.onDataLoaded();

                PostEventManager.INSTANCE.setAction(EventConstants.PULL_UP_REFRESH);
                PostEventManager.INSTANCE.setOldType(mCurrentSelectTab == TopicLandingActivity.TAB_HOT ? EventConstants.FEED_PAGE_OLD_TOPIC_HOT : EventConstants.FEED_PAGE_OLD_TOPIC_LATEST);
                PostEventManager.INSTANCE.setType(mAdapter.getFeedSource());
                PostEventManager.INSTANCE.sendEventStrategy(postBases);
            }
        });


        topicSubViewModel.getPageStatus().observe(this, new Observer<PageStatusEnum>() {
            @Override
            public void onChanged(@Nullable PageStatusEnum pageStatusEnum) {
                if (pageStatusEnum == null) {
                    return;
                }

                switch (pageStatusEnum) {
                    case EMPTY:
                        if (iTopicLandingView != null) iTopicLandingView.finishRefresh(false);
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
                        if (iTopicLandingView != null) iTopicLandingView.finishRefresh(false);
                        break;
                    case CONTENT:
                        if (iTopicLandingView != null) iTopicLandingView.finishRefresh(true);
                        if (mAdapter.getRealItemCount() == 0) {
                            showEmptyView();
                        } else {
                            showContentView();
                        }
                        break;
                    case LOADING:
                        if (mAdapter.getRealItemCount() == 0)
                            showLoading();
                        break;
                }
            }
        });

        topicSubViewModel.getLoadMoreStatus().observe(this, new Observer<PageLoadMoreStatusEnum>() {
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
    }


    public void refresh() {
        if (topicSubViewModel == null) return;
        topicSubViewModel.refresh(mCurrentSelectTab);
    }

    private void initData() {

        isBrowse = !AccountManager.getInstance().isLoginComplete();

        mTopicId = getArguments().getString(TopicConstant.BUNDLE_KEY_TOPIC_ID);

        mCurrentSelectTab = getArguments().getInt(TopicConstant.BUNDLE_KEY_TOPIC_TAB, TopicLandingActivity.TAB_HOT);

    }

    private void initViews(View view) {

        mEmptyView = view.findViewById(R.id.common_empty_view);
        mErrorView = view.findViewById(R.id.common_error_view);
        mLoadingView = view.findViewById(R.id.common_loading_view);
        mRecyclerView = view.findViewById(R.id.topic_landing_recycler_view);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            fragmentVisibleDetector.show();
            mPostViewTimeManager.onShow();
        } else {
            fragmentVisibleDetector.hide();
            mPostViewTimeManager.onHide();
        }
    }

    private void setAdapter() {

        mAdapter = new TopicLandingAdapter(null);

        mAdapter.setFeedSource(mCurrentSelectTab == TopicLandingActivity.TAB_HOT ? EventConstants.FEED_PAGE_TOPIC_HOT : EventConstants.FEED_PAGE_TOPIC_LATEST);

        if (isBrowse) {
            mAdapter.setBrowseClickListener(this);
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setRetryLoadMoreListener(new OnClickRetryListener() {
            @Override
            public void onRetryLoadMore() {
                if (canLoadMore1()) {
                    onLoadMore1();
                }
            }
        });
        mAdapter.setOnPostButtonClickListener(new OnPostButtonClickListener() {
            @Override
            public void onCommentClick(PostBase postBase) {
                MissionManager.INSTANCE.notifyEvent(MissionType.COMMENT_NEW_WELIKER);
                if (mCurrentSelectTab == TopicLandingActivity.TAB_HOT) {
                    EventLog.Feed.report7(8, postBase == null ? "" : postBase.getPid(), PostEventManager.getPostType(postBase), postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
                } else if (mCurrentSelectTab == TopicLandingActivity.TAB_LATEST) {
                    EventLog.Feed.report7(9, postBase == null ? "" : postBase.getPid(), PostEventManager.getPostType(postBase), postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
                }
                if (!AccountManager.getInstance().isLogin()) {
                    EventLog.UnLogin.report23(EventConstants.UNLOGIN_FROM_PAGE_TOPIC);
                }
            }

            @Override
            public void onLikeClick(PostBase postBase, int exp) {
                MissionManager.INSTANCE.notifyEvent(MissionType.LIKE_NEW_WELIKER);
                exp = Math.min(exp, 100);
                if (mCurrentSelectTab == TopicLandingActivity.TAB_HOT) {
                    EventLog.Feed.report5(postBase == null ? "" : postBase.getPid(),
                            postBase == null ? 0 : postBase.getSuperLikeExp(),
                            exp, 8, PostEventManager.getPostType(postBase), postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
                } else if (mCurrentSelectTab == TopicLandingActivity.TAB_LATEST) {
                    EventLog.Feed.report5(postBase == null ? "" : postBase.getPid(),
                            postBase == null ? 0 : postBase.getSuperLikeExp(),
                            exp, 9, PostEventManager.getPostType(postBase), postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
                }
                if (!AccountManager.getInstance().isLogin()) {
                    EventLog.UnLogin.report22(EventConstants.UNLOGIN_FROM_PAGE_TOPIC, null);
                }
            }

            @Override
            public void onForwardClick(PostBase postBase) {
                if (mCurrentSelectTab == TopicLandingActivity.TAB_HOT) {
                    EventLog.Feed.report6(8, postBase == null ? "" : postBase.getPid(), PostEventManager.getPostType(postBase), postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
                } else if (mCurrentSelectTab == TopicLandingActivity.TAB_LATEST) {
                    EventLog.Feed.report6(9, postBase == null ? "" : postBase.getPid(), PostEventManager.getPostType(postBase), postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
                }
            }

            @Override
            public void onFollowClick(PostBase postBase) {
                MissionManager.INSTANCE.notifyEvent(MissionType.FOLLOW_NEW_WELIKER);
                if (!AccountManager.getInstance().isLogin()) {
                    EventLog.UnLogin.report24(EventConstants.UNLOGIN_FROM_PAGE_TOPIC, postBase.getUid());
                }
            }

            @Override
            public void onShareClick(PostBase postBase, int shareType) {
//                ShareEventManager.INSTANCE.setShareType(shareType);
//                ShareEventManager.INSTANCE.setFrompage(EventConstants.SHARE_FROM_PAGE_TOPIC);
//                ShareEventManager.INSTANCE.report3();
            }

            @Override
            public void onPostBodyClick(PostBase postBase) {
//                ShareEventManager.INSTANCE.setChannel_page(EventConstants.SHARE_CHANNEL_PAGE_TOPIC);
            }

            @Override
            public void onPostAreaClick(PostBase postBase, int clickArea) {

                if (postBase == null) return;

                EventLog.Feed.report8(mCurrentSelectTab == TopicLandingActivity.TAB_HOT ? EventConstants.FEED_SOURCE_TOPIC_HOT : EventConstants.FEED_SOURCE_TOPIC_LATEST,
                        postBase.getUid(), PostEventManager.getPostRootUid(postBase),
                        postBase.getPid(), PostEventManager.getPostRootPid(postBase), clickArea,
                        PostEventManager.getPostType(postBase), postBase.getStrategy(), postBase.getSequenceId());
            }

        });
    }

    private void setEvent() {
        mErrorView.setOnErrorViewClickListener(new BaseErrorView.IErrorViewClickListener() {
            @Override
            public void onErrorViewClick() {
                PostEventManager.INSTANCE.setAction(EventConstants.CLICK_BUTTON_REFRESH);
                refresh();

            }
        });

        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(new ILoadMoreDelegate() {
            @Override
            public boolean canLoadMore() {
                return canLoadMore1();
            }

            @Override
            public void onLoadMore() {
                onLoadMore1();
            }
        }));
//        mRecyclerView.addOnScrollListener(mVideoItemVisibilityCalculator);

        mRecyclerView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                fragmentVisibleDetector.attach();
                mPostViewTimeManager.onAttach();
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                fragmentVisibleDetector.detach();
                mPostViewTimeManager.onDetach();
            }
        });

        mPostViewTimeManager.attach(mRecyclerView, (FeedRecyclerViewAdapter) mRecyclerView.getAdapter()
                , mCurrentSelectTab == TopicLandingActivity.TAB_HOT ? EventConstants.FEED_SOURCE_TOPIC_HOT : EventConstants.FEED_SOURCE_TOPIC_LATEST);


        mEmptyView.setClickable(false);
        mErrorView.setClickable(false);
        mLoadingView.setClickable(false);

    }


    public void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
        if (iTopicLandingView != null) iTopicLandingView.showLoading();
    }

    public void showErrorView() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        if (iTopicLandingView != null) iTopicLandingView.showErrorView();
    }

    public void showEmptyView() {
        // TODO: 2018/10/10
        if (mCurrentSelectTab == TopicLandingActivity.TAB_LATEST) {
            mEmptyView.showEmptyBtn(R.drawable.ic_common_empty, ResourceTool.getString("topic_empty_text"));
//                    , new EmptyView.IEmptyBtnClickListener() {
//                        @Override
//                        public void onClickEmptyBtn() {
//                            if (isBrowse) {
//                                onBrowseTopicClick(BrowseConstant.TYPE_UNKOWN, true, 0);
//                                BrowseSchemeManager.getInstance().clear();
//                                return;
//                            }
//                            if (iTopicLandingView != null) iTopicLandingView.goPublish();
//                        }
//                    });
            mLoadingView.setVisibility(View.INVISIBLE);
            mEmptyView.setVisibility(View.VISIBLE);
            mErrorView.setVisibility(View.INVISIBLE);
            if (iTopicLandingView != null) iTopicLandingView.showEmptyView();
        }
    }

    public void showContentView() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
        if (iTopicLandingView != null) iTopicLandingView.showContentView();
    }


    @Override
    public void onResume() {
        super.onResume();
        mPostViewTimeManager.onResume();
        fragmentVisibleDetector.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPostViewTimeManager.onPause();
        fragmentVisibleDetector.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPostViewTimeManager.onDestroy();
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (adapter instanceof FeedRecyclerViewAdapter) {
            ((FeedRecyclerViewAdapter) adapter).destroy();
            mVideoItemVisibilityCalculator.resetCurrentPlayItem();

        }
    }

    public boolean canScroll() {
        return ViewUtil.canScroll(mRecyclerView);
    }


    public boolean canLoadMore1() {
        return mAdapter != null && mAdapter.canLoadMore();
    }

    public void onLoadMore1() {
        if (iTopicLandingView == null) return;
        iTopicLandingView.setRefreshEnable(false);
        mAdapter.onLoadMore();

        topicSubViewModel.loadMoreHotData(mCurrentSelectTab);
    }

    @Override
    public void onBrowseClick(int tye, boolean isShow, int showType) {
        if (tye == BrowseConstant.TYPE_SHARE) {
            EventLog.UnLogin.report16(4, EventConstants.LABEL_OTHER);
        }
        onBrowseTopicClick(tye, isShow, showType);
    }

    public void onBrowseTopicClick(int tye, boolean isShow, int showType) {


    }

    public void autoPlayVideo() {
        fragmentVisibleDetector.onDataLoaded();
    }

    public void showOfflineView() {
        mEmptyView.showEmptyBtn(R.drawable.ic_common_empty, ResourceTool.getString("go_back")
                , ResourceTool.getString("topic_offline_error_text"), new EmptyView.IEmptyBtnClickListener() {
                    @Override
                    public void onClickEmptyBtn() {
                        getActivity().finish();
                    }
                });
        mLoadingView.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

}
