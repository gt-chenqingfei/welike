package com.redefine.welike.business.location.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.redefine.commonui.loadmore.adapter.EndlessRecyclerOnScrollListener;
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate;
import com.redefine.commonui.widget.BaseErrorView;
import com.redefine.commonui.widget.EmptyView;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.location.ui.adapter.LocationPasserByAdapter;
import com.redefine.welike.business.location.ui.contract.ILocationPasserByContract;

/**
 * Created by liwenbo on 2018/3/21.
 */

public class LocationPasserByView implements ILocationPasserByContract.ILocationPasserByView, View.OnClickListener
        , BaseErrorView.IErrorViewClickListener, ILoadMoreDelegate {
    private RecyclerView mRecycler;
    private View mBackBtn;
    private TextView mTitleView;
    private ILocationPasserByContract.ILocationPasserByPresenter mPresenter;
    private ErrorView mErrorView;
    private LoadingView mLoadingView;
    private EmptyView mEmptyView;

    @Override
    public View createView(Context context, Bundle savedInstanceState) {
        View view = LayoutInflater.from(context).inflate(R.layout.location_passer_by, null);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mRecycler = view.findViewById(R.id.location_passer_by_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycler.setLayoutManager(layoutManager);
        mRecycler.addOnScrollListener(new EndlessRecyclerOnScrollListener(this));
        mErrorView = view.findViewById(R.id.common_error_view);
        mEmptyView = view.findViewById(R.id.common_empty_view);
        mLoadingView = view.findViewById(R.id.common_loading_view);
        mBackBtn = view.findViewById(R.id.iv_common_back);
        mTitleView = view.findViewById(R.id.tv_common_title);

        mTitleView.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.LOCATION, "location_passer_by"));
        mBackBtn.setOnClickListener(this);

        mErrorView.setOnErrorViewClickListener(this);
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
    public void attach() {

    }

    @Override
    public void detach() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void onClick(View v) {
        if (v == mBackBtn) {
            mPresenter.onBackPressed();
        }
    }

    @Override
    public void setPresenter(ILocationPasserByContract.ILocationPasserByPresenter locationPasserByPresenter) {
        mPresenter = locationPasserByPresenter;
    }

    @Override
    public void setAdapter(LocationPasserByAdapter mAdapter) {
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onErrorViewClick() {
        mPresenter.onRefresh();
    }

    @Override
    public boolean canLoadMore() {
        return mPresenter.canLoadMore();
    }

    @Override
    public void onLoadMore() {
        mPresenter.onLoadMore();
    }

}
