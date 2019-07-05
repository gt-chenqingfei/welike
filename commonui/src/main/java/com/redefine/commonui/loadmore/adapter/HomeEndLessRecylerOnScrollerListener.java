package com.redefine.commonui.loadmore.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by mengnan on 2018/5/21.
 **/
public class HomeEndLessRecylerOnScrollerListener extends EndlessRecyclerOnScrollListener {
    public HomeEndLessRecylerOnScrollerListener(IScrollDelegate delegate) {
        super(delegate);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            int lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            if (firstVisibleItemPosition == 0) {
                ((IScrollDelegate) mLoadMoreDelegate).onSrollFirst();
            }
            if (lastVisibleItemPosition > 30) {
                ((IScrollDelegate) mLoadMoreDelegate).onSrollShowRefresh();
            }

            int totalItemCount = layoutManager.getItemCount();
            //有回调接口，且不是加载状态，且计算后剩下2个item，且处于向下滑动，则自动加载
            if (dy > 0 && totalItemCount > 2 && lastVisibleItemPosition >= totalItemCount - 5) {
                if (mLoadMoreDelegate.canLoadMore()) {
                    mLoadMoreDelegate.onLoadMore();
                }
            }
        }
    }
}
