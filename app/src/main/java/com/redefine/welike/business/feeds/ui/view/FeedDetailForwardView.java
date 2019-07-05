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
import com.redefine.commonui.widget.EmptyView;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.feeds.ui.contract.IFeedDetailForwardContract;
import com.redefine.welike.business.startup.management.HalfLoginManager;
import com.redefine.welike.commonui.event.helper.LoginEventHelper;
import com.redefine.welike.statistical.bean.RegisterAndLoginModel;

/**
 * Created by liwb on 2018/1/12.
 * 详情页的转发栏
 */

public class FeedDetailForwardView implements IFeedDetailForwardContract.IFeedDetailForwardView, ErrorView.IErrorViewClickListener {
    private RecyclerView mRecyclerView;
    private IFeedDetailForwardContract.IFeedDetailForwardPresenter mPresenter;
    private EmptyView mEmptyView;
    private ErrorView mErrorView;
    private LoadingView mLoadingView;

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void setPresenter(IFeedDetailForwardContract.IFeedDetailForwardPresenter forwardPresenter) {
        mPresenter = forwardPresenter;
    }


    @Override
    public void showLoginSnackBar(int type) {
        HalfLoginManager.getInstancce().showLoginDialog(mRecyclerView.getContext(), new RegisterAndLoginModel(LoginEventHelper.convertTypeToPageSource(type)));
//        RegisterActivity.Companion.show(mRecyclerView.getContext(),0, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.OTHER));
//        if (BrowseManager.getLoginB()) {
//            RegisterLoginMethodDialog.show(mRecyclerView.getContext());
//            return;
//        }
//
//        ActionSnackBar.getInstance().showLoginSnackBar(mRecyclerView,
//                ResourceTool.getString("common_continue_by_login"),
//                ResourceTool.getString("common_login"), 3000, new ActionSnackBar.ActionBtnClickListener() {
//                    @Override
//                    public void onActionClick(View view) {
//                        // TODO: 2018/7/11
//
//                        if (BrowseManager.getLoginA())
//                            RegisterLoginMethodDialog.show(mRecyclerView.getContext());
//                        else
//                            RegistActivity.show(mRecyclerView.getContext());
//                    }
//                });
//        RegisterActivity.Companion.show(mRecyclerView.getContext());
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
    public View createView(Context context, Bundle savedInstanceState) {
        View view = LayoutInflater.from(context).inflate(R.layout.feed_detail_forward_fragment, null);
        mRecyclerView = view.findViewById(R.id.forward_feed_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(this));
        mEmptyView = view.findViewById(R.id.common_empty_view);
        mErrorView = view.findViewById(R.id.common_error_view);
        mLoadingView = view.findViewById(R.id.common_loading_view);

        mEmptyView.setClickable(false);
        mErrorView.setClickable(false);
        mLoadingView.setClickable(false);

        mErrorView.setGravity(Gravity.TOP);
        mErrorView.setOnErrorViewClickListener(this);
        mEmptyView.showEmptyText(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "feed_detail_no_forward"));
        return view;
    }

    @Override
    public void attach() {
        mRecyclerView.startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_NON_TOUCH);
        mRecyclerView.stopNestedScroll(ViewCompat.TYPE_NON_TOUCH);
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
