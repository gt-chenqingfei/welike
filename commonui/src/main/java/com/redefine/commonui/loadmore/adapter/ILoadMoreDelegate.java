package com.redefine.commonui.loadmore.adapter;

/**
 * Created by liwenbo on 2018/2/6.
 */

public interface ILoadMoreDelegate {
    boolean canLoadMore();

    void onLoadMore();
}