package com.redefine.commonui.loadmore.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by MR on 2018/1/15.
 */

public class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    protected final ILoadMoreDelegate mLoadMoreDelegate;

    public EndlessRecyclerOnScrollListener(ILoadMoreDelegate delegate) {
        mLoadMoreDelegate = delegate;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (mLoadMoreDelegate.canLoadMore()) {
            boolean mIsScrollUp = dy > 0;
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            int lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            int totalItemCount = layoutManager.getItemCount();
            if (mIsScrollUp && totalItemCount > 2 && lastVisibleItemPosition >= totalItemCount - 5) {
                mLoadMoreDelegate.onLoadMore();
            }
        }

    }
}