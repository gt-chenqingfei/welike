package com.redefine.welike.business.publisher.ui.view;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.redefine.commonui.loadmore.adapter.EndlessRecyclerOnScrollListener;
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate;
import com.redefine.commonui.widget.BaseErrorView;
import com.redefine.commonui.widget.EmptyView;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.publisher.ui.contract.ISearchOnlineContract;

/**
 * Created by gongguan on 2018/2/27.
 */

public class SearchOnlineView implements ISearchOnlineContract.ISearchOnlineView, ILoadMoreDelegate {
    private RecyclerView mRecycler;
    private ISearchOnlineContract.ISearchOnlinePresenter mPresenter;
    private EmptyView mEmptyView;
    private ErrorView mErrorView;
    private LoadingView mLoadingView;

    @Override
    public void onCreate(final View rootView, Bundle savedInstanceState) {
        initViews(rootView);
    }

    private void initViews(final View view) {
        mRecycler = view.findViewById(R.id.contacts_search_user_recycler);
        mEmptyView = view.findViewById(R.id.common_empty_view);
        mErrorView = view.findViewById(R.id.common_error_view);
        mLoadingView = view.findViewById(R.id.common_loading_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycler.setLayoutManager(layoutManager);
        mRecycler.addOnScrollListener(new EndlessRecyclerOnScrollListener(this));

        mEmptyView.showEmptyImageText(R.drawable.ic_common_empty,
                ResourceTool.getString("publish_mention_online_list_empty"));

        mErrorView.setOnErrorViewClickListener(new BaseErrorView.IErrorViewClickListener() {
            @Override
            public void onErrorViewClick() {
                showLoading();
                mPresenter.onRefresh();
            }
        });
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public boolean canLoadMore() {
        return mPresenter.canLoadMore();
    }

    @Override
    public void onLoadMore() {
        mPresenter.loadMore();
    }

    @Override
    public void setPresenter(ISearchOnlineContract.ISearchOnlinePresenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    @Override
    public void setAdapter(RecyclerView.Adapter mAdapter) {
        mRecycler.setAdapter(mAdapter);
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
    public void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }
}
