package com.redefine.commonui.loadmore.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by MR on 2018/1/15.
 */

public class ProfilePhotoLoadMoreOnScrollListener extends RecyclerView.OnScrollListener {

    private boolean mIsScrollUp;
    protected final ILoadMoreDelegate mLoadMoreDelegate;

    public ProfilePhotoLoadMoreOnScrollListener(ILoadMoreDelegate delegate) {
        mLoadMoreDelegate = delegate;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        mIsScrollUp = dy > 0;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            //有回调接口，且不是加载状态，且计算后剩下2个item，且处于向下滑动，则自动加载
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                int lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();
                if (mIsScrollUp && lastVisibleItemPosition >= totalItemCount - 1)
                if (mLoadMoreDelegate.canLoadMore()) {
                    mLoadMoreDelegate.onLoadMore();
                }
            }
        }
    }
}