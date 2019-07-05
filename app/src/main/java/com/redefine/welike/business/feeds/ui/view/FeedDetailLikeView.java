package com.redefine.welike.business.feeds.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.redefine.commonui.loadmore.adapter.EndlessRecyclerOnScrollListener;
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate;
import com.redefine.commonui.loadmore.adapter.LoadMoreFooterRecyclerAdapter;
import com.redefine.commonui.widget.EmptyView;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.feeds.ui.contract.IFeedDetailLikeContract;

/**
 * Created by MR on 2018/1/20.
 */

public class FeedDetailLikeView implements IFeedDetailLikeContract.IFeedDetailLikeView, ILoadMoreDelegate, ErrorView.IErrorViewClickListener {

    private RecyclerView mRecyclerView;
    private IFeedDetailLikeContract.IFeedDetailLikePresenter mPresenter;
    private EmptyView mEmptyView;
    private ErrorView mErrorView;
    private LoadingView mLoadingView;

    @Override
    public View createView(Context context, Bundle savedInstanceState) {
        View view = LayoutInflater.from(context).inflate(R.layout.feed_detail_like_fragment, null);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mRecyclerView = view.findViewById(R.id.feed_detail_like_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(this));
        mEmptyView = view.findViewById(R.id.common_empty_view);
        mErrorView = view.findViewById(R.id.common_error_view);
        mLoadingView = view.findViewById(R.id.common_loading_view);

        mEmptyView.setClickable(false);
        mErrorView.setClickable(false);
        mLoadingView.setClickable(false);

        mEmptyView.showEmptyText(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "feed_detail_no_like"));
        mErrorView.setGravity(Gravity.TOP);
        mErrorView.setOnErrorViewClickListener(this);
    }

    public void setAdapter(LoadMoreFooterRecyclerAdapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void setPresenter(IFeedDetailLikeContract.IFeedDetailLikePresenter likePresenter) {
        mPresenter = likePresenter;
    }

    @Override
    public void attach() {
        mRecyclerView.startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_NON_TOUCH);
        mRecyclerView.stopNestedScroll(ViewCompat.TYPE_NON_TOUCH);
    }

    @Override
    public void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showErrorView() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showEmptyView() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showContent() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void detach() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public boolean canLoadMore() {
        return mPresenter.canLoadMore();
    }

    @Override
    public void onLoadMore() {
        mPresenter.onLoadMore();
    }

    @Override
    public void onErrorViewClick() {
        mPresenter.onRefresh();
    }
}
