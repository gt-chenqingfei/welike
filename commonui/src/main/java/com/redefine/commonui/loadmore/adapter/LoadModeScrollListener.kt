package com.redefine.commonui.loadmore.adapter

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate

/**
 *
 * Name: LoadModeScrollListener
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-07-10 15:25
 *
 */
class LoadModeScrollListener(private val mLoadMoreDelegate: ILoadMoreDelegate) : RecyclerView.OnScrollListener() {

    private var mIsScrollUp: Boolean = false

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        mIsScrollUp = dy > 0
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        val layoutManager = recyclerView!!.layoutManager
        if (layoutManager is LinearLayoutManager) {
            //有回调接口，且不是加载状态，且计算后剩下2个item，且处于向下滑动，则自动加载
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.getItemCount()
                if (mIsScrollUp && totalItemCount > 2 && lastVisibleItemPosition >= totalItemCount - 2)
                    if (mLoadMoreDelegate.canLoadMore()) {
                        mLoadMoreDelegate.onLoadMore()
                    }
            }
        }
    }
}