package com.redefine.welike.business.supertopic.ui.page;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.BasePage;
import com.redefine.commonui.loadmore.adapter.EndlessRecyclerOnScrollListener;
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate;
import com.redefine.commonui.util.ViewUtil;
import com.redefine.commonui.widget.EmptyView;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.multimedia.player.apollo.ApolloVideoView;
import com.redefine.welike.R;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.business.browse.ui.listener.IBrowseClickListener;
import com.redefine.welike.business.common.mission.OnPostButtonClickListener;
import com.redefine.welike.business.feeds.management.SinglePostsManager;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter;
import com.redefine.welike.business.feeds.ui.util.FeedHelper;
import com.redefine.welike.commonui.event.expose.ItemExposeManager;
import com.redefine.welike.business.startup.management.HalfLoginManager;
import com.redefine.welike.business.supertopic.management.provider.SuperTopicHotFeedsProvider;
import com.redefine.welike.business.supertopic.ui.adapter.SuperTopicRecylerViewAdapter;
import com.redefine.welike.commonui.event.helper.LoginEventHelper;
import com.redefine.welike.commonui.event.expose.impl.ItemExposeCallbackFactory;
import com.redefine.welike.commonui.view.VideoItemVisibilityCalculator;
import com.redefine.welike.frameworkmvvm.BaseLifecycleFragmentPage;
import com.redefine.welike.frameworkmvvm.BaseLifecyclePageStackManager;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.bean.RegisterAndLoginModel;
import com.redefine.welike.statistical.manager.PostEventManager;
import com.redefine.welike.statistical.manager.ShareEventManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

public class SuperTopicTrendingPage extends BaseLifecycleFragmentPage implements SinglePostsManager.PostsCallback, ILoadMoreDelegate, ErrorView.IErrorViewClickListener, IBrowseClickListener, OnPostButtonClickListener {
    private RecyclerView recyclerView;
    private SmartRefreshLayout refreshLayout;
    private EmptyView emptyView;
    private ErrorView errorView;
    private LoadingView loadingView;
    private final SinglePostsManager postModel = new SinglePostsManager();
    private SuperTopicRecylerViewAdapter adapter;
    private String topicId;
    private ItemExposeManager mPostViewTimeManager = new ItemExposeManager(ItemExposeCallbackFactory.Companion.createPostExposeCallback(),
            ItemExposeCallbackFactory.Companion.createPostViewCallback(),
            ItemExposeCallbackFactory.Companion.createPostFollowBtnCallback());

    private VideoItemVisibilityCalculator videoItemVisibilityCalculator = new VideoItemVisibilityCalculator(new VideoItemVisibilityCalculator.VideoPlayStateCallback() {
        @Override
        public void stopPlayback() {
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            if (adapter instanceof FeedRecyclerViewAdapter) {
                ((FeedRecyclerViewAdapter) adapter).destroyVideo();
                videoItemVisibilityCalculator.resetCurrentPlayItem();
            }
        }

        @Override
        public void startNewPlay(int position, ApolloVideoView videoPlayerView) {
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            if (adapter instanceof FeedRecyclerViewAdapter) {
                ((FeedRecyclerViewAdapter) adapter).playVideo(position, videoPlayerView);
            }
        }
    });

    public SuperTopicTrendingPage(BaseLifecyclePageStackManager stackManager, PageConfig config, PageCache cache) {
        super(stackManager, config, cache);
    }

    @NonNull
    @Override
    protected View createView(ViewGroup container, Bundle saveState) {
        return mPageStackManager.getLayoutInflater().inflate(R.layout.super_topic_trending_fragment_layout, null);
    }

    @Override
    public Bundle onSaveInstanceState() {
        return mPageConfig.pageBundle;
    }

    @Override
    protected void initView(ViewGroup container, Bundle saveState) {
        super.initView(container, saveState);

        if (TextUtils.isEmpty(topicId)) {
            if (mPageConfig.pageBundle != null) {
                topicId = mPageConfig.pageBundle.getString("topicId");
            } else if (saveState != null) {
                topicId = saveState.getString("topicId");
            }
        }

        boolean isBrowse = !AccountManager.getInstance().isLogin();

        recyclerView = getView().findViewById(R.id.recycler_trending);
        refreshLayout = getView().findViewById(R.id.feed_refresh_layout);
        emptyView = getView().findViewById(R.id.common_empty_view);
        errorView = getView().findViewById(R.id.common_error_view);
        loadingView = getView().findViewById(R.id.common_loading_view);

        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(this));
//        recyclerView.addOnScrollListener(videoItemVisibilityCalculator);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setEnableOverScrollBounce(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(container.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        errorView.setOnErrorViewClickListener(this);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                SuperTopicTrendingPage.this.onRefresh();
                PostEventManager.INSTANCE.setAction(EventConstants.PULL_DOWN_REFRESH);
            }

        });
        recyclerView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {

            @Override
            public void onViewAttachedToWindow(View v) {
                videoItemVisibilityCalculator.onScrollStateChanged(recyclerView, RecyclerView.SCROLL_STATE_IDLE);
                mPostViewTimeManager.onAttach();
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                RecyclerView.Adapter adapter = recyclerView.getAdapter();
                if (adapter instanceof FeedRecyclerViewAdapter) {
                    ((FeedRecyclerViewAdapter) adapter).destroyVideo();
                    videoItemVisibilityCalculator.resetCurrentPlayItem();

                }
                mPostViewTimeManager.onDetach();
            }

        });

        emptyView.setClickable(false);
        errorView.setClickable(false);
        loadingView.setClickable(false);

        postModel.setDataSourceProvider(new SuperTopicHotFeedsProvider(topicId));
        postModel.setListener(this);

        adapter = new SuperTopicRecylerViewAdapter(mPageStackManager, EventConstants.FEED_PAGE_SUPER_TOPIC_HOT);
        if (isBrowse) {
            adapter.setBrowseClickListener(this);
        } else {
            adapter.setBrowseClickListener(null);
        }
        recyclerView.setAdapter(adapter);
        adapter.setOnPostButtonClickListener(this);

        onRefresh();
        PostEventManager.INSTANCE.setAction(EventConstants.AUTO_REFRESH);
        mPostViewTimeManager.attach(recyclerView, adapter, EventConstants.FEED_SOURCE_SUPER_TOPIC_HOT);
    }

    @Override
    public void onRefreshPosts(SinglePostsManager manager, List<PostBase> posts, int newCount, boolean last, int errCode) {
        boolean isSuccess = errCode == ErrorCode.ERROR_SUCCESS;
        if (isSuccess) {
            adapter.addNewData(posts);
            adapter.clearFinishFlag();
            if (adapter.getRealItemCount() == 0) {
                // 空页面
                showEmptyView();
            } else {
                showContentView();
            }
            if (last) {
                if (canScroll()) {
                    adapter.noMore();
                } else {
                    adapter.clearFinishFlag();
                }
            } else {
                adapter.clearFinishFlag();
            }
        } else {
            // 网络失败给用户提醒
            if (adapter.getRealItemCount() == 0) {
                // 空页面
                showErrorView();
            } else {
                showContentView();
            }
        }
        finishRefresh(isSuccess);

        adapter.showFooter();
        mPostViewTimeManager.setData(posts, false);
        PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_SUPER_TOPIC_HOT);
        PostEventManager.INSTANCE.setType(adapter.getFeedSource());
        PostEventManager.INSTANCE.sendEventStrategy(FeedHelper.subPosts(posts, newCount));
    }

    @Override
    public void onReceiveHisPosts(SinglePostsManager manager, List<PostBase> posts, boolean last, int errCode) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            adapter.addHisData(posts);
            if (last) {
                adapter.noMore();
            } else {
                adapter.finishLoadMore();
            }
        } else {
            adapter.loadError();
        }
        refreshLayout.setEnableRefresh(true);
        mPostViewTimeManager.updateData(posts);
        PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_SUPER_TOPIC_HOT);
        PostEventManager.INSTANCE.setType(adapter.getFeedSource());
        PostEventManager.INSTANCE.sendEventStrategy(posts);
    }

    @Override
    public boolean canLoadMore() {
        return adapter.canLoadMore();
    }

    @Override
    public void onLoadMore() {
        refreshLayout.setEnableRefresh(false);
        adapter.onLoadMore();
        PostEventManager.INSTANCE.setAction(EventConstants.PULL_UP_REFRESH);
        hisPost();
    }

    @Override
    public void onErrorViewClick() {
        onRefresh();
        PostEventManager.INSTANCE.setAction(EventConstants.CLICK_BUTTON_REFRESH);
    }

    @Override
    public void destroy(ViewGroup container) {
        super.destroy(container);
        if (adapter != null) {
            adapter.destroyVideo();
            videoItemVisibilityCalculator.resetCurrentPlayItem();
        }
        if (mPostViewTimeManager != null) {
            mPostViewTimeManager.onDestroy();
        }
    }

    @Override
    public void onActivityResume() {
        super.onActivityResume();
        if (adapter != null) {
            adapter.onResume();
        }
        mPostViewTimeManager.onResume();
    }

    @Override
    public void onActivityPause() {
        super.onActivityPause();
        if (adapter != null) {
            adapter.destroyVideo();
            videoItemVisibilityCalculator.resetCurrentPlayItem();
        }
        mPostViewTimeManager.onPause();
    }

    @Override
    public void onBasePageStateChanged(int oldPageState, int pageState) {
        if (pageState == BasePage.PAGE_STATE_SHOW) {
            if (adapter != null) {
                adapter.onResume();
            }
        } else {
            if (adapter != null) {
                adapter.destroyVideo();
                videoItemVisibilityCalculator.resetCurrentPlayItem();
            }
        }
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            mPostViewTimeManager.onResume();
            mPostViewTimeManager.onShow();
        } else {
            mPostViewTimeManager.onPause();
            mPostViewTimeManager.onHide();
        }
    }

    @Override
    public void onBrowseClick(int tye, boolean isShow, int showType) {
        if (isShow) {
            HalfLoginManager.getInstancce().showLoginDialog(mPageStackManager.getContext(), new RegisterAndLoginModel(LoginEventHelper.convertTypeToPageSource(tye)));
//            RegisterActivity.Companion.show(mPageStackManager.getContext(), 0, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.OTHER));
        }

    }

    private void onRefresh() {
        if (adapter.getRealItemCount() == 0) {
            showLoading();
        }
        adapter.hideFooter();
        refreshPost();
    }

    private void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
    }

    private void showErrorView() {
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }

    private void showEmptyView() {
        emptyView.showEmptyImageText(R.drawable.ic_common_empty, getView().getResources().getText(R.string.super_topic_empty).toString());
        loadingView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
    }

    private void showContentView() {
        loadingView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
    }

    private void refreshPost() {
        postModel.tryRefreshPosts();
    }

    private void hisPost() {
        postModel.tryHisPosts();
    }

    private boolean canScroll() {
        return ViewUtil.canScroll(recyclerView);
    }

    private void finishRefresh(boolean isSuccess) {
        refreshLayout.finishRefresh(300, isSuccess);
    }

    @Override
    public void onCommentClick(PostBase postBase) {
        EventLog.Feed.report6(16, postBase == null ? "" : postBase.getPid(), PostEventManager.getPostType(postBase),
                postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
    }

    @Override
    public void onLikeClick(PostBase postBase, int exp) {
        EventLog.Feed.report5(postBase == null ? "" : postBase.getPid(),
                postBase == null ? 0 : postBase.getSuperLikeExp(), exp, 16,
                PostEventManager.getPostType(postBase), postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
    }

    @Override
    public void onForwardClick(PostBase postBase) {
        EventLog.Feed.report7(16, postBase == null ? "" : postBase.getPid(), PostEventManager.getPostType(postBase),
                postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
    }

    @Override
    public void onFollowClick(PostBase postBase) {

    }

    @Override
    public void onShareClick(PostBase postBase, int shareType) {

    }

    @Override
    public void onPostBodyClick(PostBase postBase) {
        ShareEventManager.INSTANCE.setChannel_page(EventConstants.SHARE_CHANNEL_PAGE_TOPIC);
    }

    @Override
    public void onPostAreaClick(PostBase postBase, int clickArea) {
        EventLog.Feed.report8(EventConstants.FEED_SOURCE_SUPER_TOPIC_HOT,
                postBase.getUid(), PostEventManager.getPostRootUid(postBase),
                postBase.getPid(), PostEventManager.getPostRootPid(postBase), clickArea,
                PostEventManager.getPostType(postBase), postBase.getStrategy(), postBase.getSequenceId());
    }
}
