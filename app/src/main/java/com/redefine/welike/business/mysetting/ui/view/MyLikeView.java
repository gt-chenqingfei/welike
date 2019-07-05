package com.redefine.welike.business.mysetting.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.redefine.commonui.loadmore.adapter.EndlessRecyclerOnScrollListener;
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate;
import com.redefine.commonui.widget.EmptyView;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.multimedia.player.apollo.ApolloVideoView;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter;
import com.redefine.welike.commonui.event.expose.ItemExposeManager;
import com.redefine.welike.business.mysetting.ui.contract.IMyLikeContract;
import com.redefine.welike.commonui.view.ClassicsHeader1;
import com.redefine.welike.commonui.view.VideoItemVisibilityCalculator;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.manager.PostEventManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

/**
 * Created by gongguan on 2018/2/23.
 */

public class MyLikeView implements IMyLikeContract.IMyLikeView, ILoadMoreDelegate {
    private View view;
    private IMyLikeContract.IMyLikePresenter mPresenter;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private EmptyView mEmptyView;
    private ErrorView mErrorView;
    private LoadingView mLoadingView;
    private ClassicsHeader1 mClassHeadview;
    private ItemExposeManager mPostViewTimeManager;
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

    public MyLikeView() {
    }

    @Override
    public View createView(Context context, Bundle savedInstanceState) {
        view = LayoutInflater.from(context).inflate(R.layout.setting_my_like_page, null);

        initViews();
        return view;
    }

    private void initViews() {
        TextView title = view.findViewById(R.id.tv_common_title);

        mRefreshLayout = view.findViewById(R.id.user_my_like_refresh_layout);
        mRecyclerView = view.findViewById(R.id.user_my_like_recycler_view);
        view.findViewById(R.id.ll_common_layout).setBackgroundResource(R.color.white);
        mEmptyView = view.findViewById(R.id.common_empty_view);
        mErrorView = view.findViewById(R.id.common_error_view);
        mLoadingView = view.findViewById(R.id.common_loading_view);
        mClassHeadview = view.findViewById(R.id.user_my_like_refresh_header);
        ImageView mBack = view.findViewById(R.id.iv_common_back);

        mEmptyView.showEmptyImageText(R.drawable.ic_common_empty,
                ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_no_information_text"));
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(this));
//        mRecyclerView.addOnScrollListener(mVideoItemVisibilityCalculator);

        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setEnableOverScrollBounce(false);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mPresenter.onRefresh();
                PostEventManager.INSTANCE.setAction(EventConstants.PULL_DOWN_REFRESH);
            }
        });

        mErrorView.setOnErrorViewClickListener(new ErrorView.IErrorViewClickListener() {
            @Override
            public void onErrorViewClick() {
                showLoading();
                mPresenter.onRefresh();
                PostEventManager.INSTANCE.setAction(EventConstants.CLICK_BUTTON_REFRESH);
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onBackPressed();
            }
        });
        title.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_invite_friends_text"));
        mRecyclerView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                mVideoItemVisibilityCalculator.onScrollStateChanged(mRecyclerView, RecyclerView.SCROLL_STATE_IDLE);
                if (mPostViewTimeManager != null) {
                    mPostViewTimeManager.onAttach();
                }
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
                if (adapter != null && adapter instanceof FeedRecyclerViewAdapter) {
                    ((FeedRecyclerViewAdapter) adapter).destroyVideo();
                    mVideoItemVisibilityCalculator.resetCurrentPlayItem();

                }
                if (mPostViewTimeManager != null) {
                    mPostViewTimeManager.onDetach();
                }
            }
        });
    }

    @Override
    public void attach() {
        PostEventManager.INSTANCE.reset();
    }

    @Override
    public void detach() {

    }

    @Override
    public void destroy() {
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (adapter instanceof FeedRecyclerViewAdapter) {
            ((FeedRecyclerViewAdapter) adapter).destroyVideo();
            mVideoItemVisibilityCalculator.resetCurrentPlayItem();

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
    public void setPresenter(IMyLikeContract.IMyLikePresenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    @Override
    public void setAdapter(RecyclerView.Adapter mAdapter) {
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void finishRefresh() {
        mRefreshLayout.finishRefresh();
    }

    @Override
    public void autoRefresh() {
        mRefreshLayout.autoRefresh();
        PostEventManager.INSTANCE.setAction(EventConstants.AUTO_REFRESH);
    }

    @Override
    public void setRefreshEnable(boolean b) {
        mRefreshLayout.setEnableRefresh(b);
    }

    @Override
    public void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
        mRefreshLayout.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showContent() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mRefreshLayout.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showEmptyView() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mRefreshLayout.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.VISIBLE);
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
    public void setRefreshCount(int size) {
//        mClassHeadview.setRefreshCount(size);
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
    public void onActivityResume() {
        mVideoItemVisibilityCalculator.onScrollStateChanged(mRecyclerView, RecyclerView.SCROLL_STATE_IDLE);
    }

    @Override
    public void onActivityPause() {
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (adapter != null && adapter instanceof FeedRecyclerViewAdapter) {
            ((FeedRecyclerViewAdapter) adapter).destroyVideo();
            mVideoItemVisibilityCalculator.resetCurrentPlayItem();

        }
    }

    @Override
    public void setRecyclerViewManager(ItemExposeManager manager) {
        mPostViewTimeManager = manager;
        manager.attach(mRecyclerView, (FeedRecyclerViewAdapter) mRecyclerView.getAdapter(), EventConstants.FEED_SOURCE_ME_LIKES);
    }
}
