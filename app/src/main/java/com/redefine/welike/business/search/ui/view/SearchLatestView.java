package com.redefine.welike.business.search.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.pekingese.pagestack.framework.page.BasePage;
import com.redefine.commonui.loadmore.adapter.EndlessRecyclerOnScrollListener;
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate;
import com.redefine.commonui.widget.BaseErrorView;
import com.redefine.commonui.widget.EmptyView;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.multimedia.player.apollo.ApolloVideoView;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter;
import com.redefine.welike.commonui.event.expose.ItemExposeManager;
import com.redefine.welike.business.search.ui.adapter.SearchLatestAdapter;
import com.redefine.welike.business.search.ui.contract.ISearchLatestContract;
import com.redefine.welike.commonui.view.VideoItemVisibilityCalculator;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.manager.PostEventManager;

/**
 * Created by liwenbo on 2018/2/11.
 */

public class SearchLatestView implements ISearchLatestContract.ISearchLatestView, ILoadMoreDelegate {

    private RecyclerView mRecyclerView;
    private ISearchLatestContract.ISearchLatestPresenter mPresenter;
    private EmptyView mEmptyView;
    private ErrorView mErrorView;
    private LoadingView mLoadingView;
    private ItemExposeManager mPostViewTimeManager;
    private VideoItemVisibilityCalculator mVideoItemVisibilityCalculator =  new VideoItemVisibilityCalculator(new VideoItemVisibilityCalculator.VideoPlayStateCallback() {
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

    @Override
    public View createView(Context context, Bundle savedInstanceState) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_latest_layout, null);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mRecyclerView = view.findViewById(R.id.search_latest_recycler_view);
        mEmptyView = view.findViewById(R.id.common_empty_view);
        mErrorView = view.findViewById(R.id.common_error_view);
        mLoadingView = view.findViewById(R.id.common_loading_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(this));
//        mRecyclerView.addOnScrollListener(mVideoItemVisibilityCalculator);
        mEmptyView.showEmptyImageText(R.drawable.ic_common_empty, ResourceTool.getString(ResourceTool.ResourceFileEnum.SEARCH, "search_no_result"));
        mErrorView.setOnErrorViewClickListener(new BaseErrorView.IErrorViewClickListener() {
            @Override
            public void onErrorViewClick() {
                mPresenter.retryRefresh();
                PostEventManager.INSTANCE.setAction(EventConstants.CLICK_BUTTON_REFRESH);
            }
        });
        mRecyclerView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                mVideoItemVisibilityCalculator.onScrollStateChanged(mRecyclerView, RecyclerView.SCROLL_STATE_IDLE);
                if (mPostViewTimeManager != null) {
                    mPostViewTimeManager.onResume();
                    mPostViewTimeManager.onAttach();
                    mPostViewTimeManager.onShow();
                }
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
                if (adapter instanceof FeedRecyclerViewAdapter) {
                    ((FeedRecyclerViewAdapter) adapter).destroyVideo();
                    mVideoItemVisibilityCalculator.resetCurrentPlayItem();

                }
                if (mPostViewTimeManager != null) {
                    mPostViewTimeManager.onPause();
                    mPostViewTimeManager.onDetach();
                    mPostViewTimeManager.onHide();
                }
            }
        });
    }

    @Override
    public void attach() {
        PostEventManager.INSTANCE.setAction(EventConstants.AUTO_REFRESH);
    }

    @Override
    public void detach() {

    }

    @Override
    public void destroy() {
    }

    @Override
    public void setAdapter(SearchLatestAdapter mAdapter) {
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void setPresenter(ISearchLatestContract.ISearchLatestPresenter searchLatestPresenter) {
        mPresenter = searchLatestPresenter;
    }

    @Override
    public void showErrorView() {
        mEmptyView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoading() {
        mEmptyView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.GONE);
    }

    @Override
    public void showEmptyView() {
        mEmptyView.setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
    }

    @Override
    public void showContentView() {
        mEmptyView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
    }

    @Override
    public void autoPlayVideo() {
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mVideoItemVisibilityCalculator.onScrollStateChanged(mRecyclerView, RecyclerView.SCROLL_STATE_IDLE);
            }
        });
    }

    @Override
    public View getView() {
        return mRecyclerView;
    }

    @Override
    public void onActivityResume() {
        mVideoItemVisibilityCalculator.onScrollStateChanged(mRecyclerView, RecyclerView.SCROLL_STATE_IDLE);
    }

    @Override
    public void onActivityPause() {
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (adapter instanceof FeedRecyclerViewAdapter) {
            ((FeedRecyclerViewAdapter) adapter).destroyVideo();
            mVideoItemVisibilityCalculator.resetCurrentPlayItem();

        }
    }

    @Override
    public void onPageStateChanged(int oldState, int newState) {
        if (newState == BasePage.PAGE_STATE_SHOW) {
            mVideoItemVisibilityCalculator.onScrollStateChanged(mRecyclerView, RecyclerView.SCROLL_STATE_IDLE);
        } else {
            RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
            if (adapter instanceof FeedRecyclerViewAdapter) {
                ((FeedRecyclerViewAdapter) adapter).destroyVideo();
                mVideoItemVisibilityCalculator.resetCurrentPlayItem();

            }
        }
    }

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
    public void setRecyclerViewManager(ItemExposeManager manager) {
        if(mRecyclerView==null)return;
        mPostViewTimeManager = manager;
        manager.attach(mRecyclerView, (FeedRecyclerViewAdapter) mRecyclerView.getAdapter(), EventConstants.FEED_SOURCE_SEARCH_LATEST);
    }
}
