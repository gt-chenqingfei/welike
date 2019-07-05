package com.redefine.welike.business.user.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.redefine.commonui.loadmore.adapter.EndlessRecyclerOnScrollListener;
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate;
import com.redefine.commonui.widget.EmptyView;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.commonui.event.expose.ItemExposeManager;
import com.redefine.welike.business.publisher.ui.activity.PublishPostStarter;
import com.redefine.welike.business.user.ui.adapter.UserFollowRecyclerAdapter;
import com.redefine.welike.business.user.ui.contract.IUserFollowerContract;
import com.redefine.welike.statistical.EventLog1;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

/**
 * Created by gongguan on 2018/1/22.
 */

public class UserFollowerView implements IUserFollowerContract.IUserFollowerView, ILoadMoreDelegate {
    private IUserFollowerContract.IUserFollowerPresenter mPresenter;
    private View mView;
    private RecyclerView recyclerView;
    private SmartRefreshLayout mRefreshLayout;
    private EmptyView mEmptyView;
    private ErrorView mErrorView;
    private LoadingView mLoadingView;

    public UserFollowerView() {
    }

    @Override
    public View createView(Context context, Bundle savedInstanceState) {
        mView = LayoutInflater.from(context).inflate(R.layout.user_following_recyclerview, null);

        initViews();
        bindEvents();

        return mView;
    }

    public void initViews() {
        recyclerView = mView.findViewById(R.id.recycler_user_following_follow);
        mRefreshLayout = mView.findViewById(R.id.pullLayout_user_following_follow);
        mEmptyView = mView.findViewById(R.id.common_empty_view);
        mErrorView = mView.findViewById(R.id.common_error_view);
        mLoadingView = mView.findViewById(R.id.common_loading_view);

        mEmptyView.showEmptyBtn(R.drawable.ic_common_empty,
                ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "user_follower_page_empty_btn"),
                ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "user_follower_page_empty"),
                new EmptyView.IEmptyBtnClickListener() {

                    @Override
                    public void onClickEmptyBtn() {
                        PublishPostStarter.INSTANCE.startActivityFromMain(mView.getContext(), EventLog1.Publish.MainSource.DISCOVER);
                    }

                });

        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(this));
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setEnableOverScrollBounce(false);

        mErrorView.setOnErrorViewClickListener(new ErrorView.IErrorViewClickListener() {
            @Override
            public void onErrorViewClick() {
                showLoading();
                mPresenter.onRefresh();
            }
        });

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mPresenter.onRefresh();
            }
        });
    }

    public void bindEvents() {
        LinearLayoutManager llManager = new LinearLayoutManager(mView.getContext());
        llManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llManager);

        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setEnableOverScrollBounce(false);

    }

    @Override
    public void setAdapter(UserFollowRecyclerAdapter mAdapter, ItemExposeManager callback) {
        recyclerView.setAdapter(mAdapter);
        callback.attach(recyclerView, mAdapter, null);
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
    public void setPresenter(IUserFollowerContract.IUserFollowerPresenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    @Override
    public void finishRefresh() {
        mRefreshLayout.finishRefresh();
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
    public boolean canLoadMore() {
        return mPresenter.canLoadMore();
    }

    @Override
    public void onLoadMore() {
        mPresenter.onLoadMore();
    }
}
