package com.redefine.welike.business.search.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.redefine.commonui.loadmore.adapter.EndlessRecyclerOnScrollListener;
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate;
import com.redefine.commonui.widget.BaseErrorView;
import com.redefine.commonui.widget.EmptyView;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.search.ui.adapter.SearchUserAdapter;
import com.redefine.welike.business.search.ui.contract.ISearchUserContract;

/**
 * Created by liwenbo on 2018/2/11.
 */

public class SearchUserView implements ISearchUserContract.ISearchUserView, ILoadMoreDelegate {


    private RecyclerView mRecyclerView;
    private ISearchUserContract.ISearchUserPresenter mPresenter;
    private EmptyView mEmptyView;
    private ErrorView mErrorView;
    private LoadingView mLoadingView;

    @Override
    public View createView(Context context, Bundle savedInstanceState) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_layout, null);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mRecyclerView = view.findViewById(R.id.search_user_recycler_view);
        mEmptyView = view.findViewById(R.id.common_empty_view);
        mErrorView = view.findViewById(R.id.common_error_view);
        mLoadingView = view.findViewById(R.id.common_loading_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(this));
        mEmptyView.showEmptyImageText(R.drawable.ic_common_empty, ResourceTool.getString(ResourceTool.ResourceFileEnum.SEARCH, "search_no_result"));
        mErrorView.setOnErrorViewClickListener(new BaseErrorView.IErrorViewClickListener() {
            @Override
            public void onErrorViewClick() {
                mPresenter.retryRefresh();
            }
        });
    }

    @Override
    public void attach() {

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
    public void setPresenter(ISearchUserContract.ISearchUserPresenter searchUserPresenter) {
        mPresenter = searchUserPresenter;
    }

    @Override
    public void setAdapter(SearchUserAdapter mAdapter) {
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void showErrorView() {
        mEmptyView.setVisibility(View.INVISIBLE);
        mLoadingView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoading() {
        mEmptyView.setVisibility(View.INVISIBLE);
        mLoadingView.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showEmptyView() {
        mEmptyView.setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showContentView() {
        mEmptyView.setVisibility(View.INVISIBLE);
        mLoadingView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }
}
