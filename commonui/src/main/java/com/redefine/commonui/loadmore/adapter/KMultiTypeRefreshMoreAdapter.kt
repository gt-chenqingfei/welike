package com.redefine.commonui.loadmore.adapter

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.redefine.commonui.loadmore.bean.LoadMoreBean
import com.redefine.foundation.utils.CollectionUtil
import com.redefine.welike.commonui.adapter.KViewHolder

class KMultiTypeRefreshMoreAdapter<T>(private val factory: KMultiTypeLoadMoreFactory<T>, private val init: (View, T, Int) -> Unit, private val more: (View, LoadMoreBean) -> Unit?) :
        RecyclerView.Adapter<KViewHolder>() {

    private var isRefreshingMore: Boolean = false
    private var refreshMoreBean = LoadMoreBean()
    private var mRecycler: RecyclerView? = null
    private var showHeader = true
    var datas = mutableListOf<T>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KViewHolder {
        if (factory.isLoadMoreType(viewType)) {
            val view = LayoutInflater.from(parent.context).inflate(factory.loadMoreLayoutOf(), parent, false)
            return factory.loadMoreHolderOf(view)
        }
        val view = LayoutInflater.from(parent.context).inflate(factory.layoutOf(viewType), parent, false)
        return factory.holderOf(view)
    }

    override fun onBindViewHolder(holder: KViewHolder, position: Int) {
        if (factory.isLoadMoreType(getItemViewType(position))) {
            val loadMoreViewHolder: KLoadMoreViewHolder = holder as KLoadMoreViewHolder
            loadMoreViewHolder.bindLoadMore(refreshMoreBean, more)
        } else {
            if (hasHeader()) {
                holder.bindForecast(datas[position - 1], init)
            } else {
                holder.bindForecast(datas[position], init)
            }
        }
    }

    fun setData(mutableList: MutableList<T>) {
        datas.clear()
        if(CollectionUtil.isEmpty(mutableList)) {
            return
        }
        datas.addAll(mutableList)
        notifyDataSetChanged()
    }

    fun insertLatest(mutableList: MutableList<T>) {
         if(CollectionUtil.isEmpty(mutableList)) {
             return
         }
        datas.addAll(mutableList)
        notifyDataSetChanged()
    }

    fun addHisData(mutableList: MutableList<T>) {
        if(CollectionUtil.isEmpty(mutableList)) {
            return
        }
        datas.addAll(0, mutableList)
        notifyDataSetChanged()
    }

    /**
     * Call this method to show hiding footer.
     */
    fun showHeader() {
        this.showHeader = true
        notifyDataSetChanged()
    }

    /**
     * Returns true if the footer configured is not null.
     */
    private fun hasHeader(): Boolean {
        return showHeader
    }

    /**
     * Call this method to hide footer.
     */
    fun hideHeader() {
        this.showHeader = false
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        if (CollectionUtil.isEmpty(datas)) {
            return 0
        }
        return if (hasHeader())
            datas.size + 1
        else
            datas.size

    }

    override fun getItemViewType(position: Int): Int {
        return if (isHeaderPosition(position)) {
            factory.loadMoreTypeOf()
        } else {
            if (hasHeader()) {
                factory.typeOf(datas[position - 1])
            } else {
                factory.typeOf(datas[position])
            }
        }
    }

    private fun getHeaderPosition(): Int {
        return 0
    }

    fun onRefreshMore() {
        isRefreshingMore = true
        refreshMoreBean.state = LoadMoreBean.LoadingMoreState.LOADING
        notifyItemChanged(getHeaderPosition())
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        mRecycler = null
        super.onDetachedFromRecyclerView(recyclerView)
    }

    fun finishRefreshMore() {
        isRefreshingMore = false
        refreshMoreBean.state = (LoadMoreBean.LoadingMoreState.LOADING)
        notifyItemChanged(getHeaderPosition())
    }

    fun refreshError() {
        isRefreshingMore = false
        refreshMoreBean.state = (LoadMoreBean.LoadingMoreState.LOADED_ERROR)
        notifyItemChanged(getHeaderPosition())
    }

    fun noMore() {
        isRefreshingMore = false
        refreshMoreBean.state = (LoadMoreBean.LoadingMoreState.NO_MORE)
        notifyItemChanged(getHeaderPosition())
    }

    fun clearFinishFlag() {
        isRefreshingMore = false
        refreshMoreBean.state = (LoadMoreBean.LoadingMoreState.NONE)
        notifyItemChanged(getHeaderPosition())
    }

    fun goneRefreshMore() {
        isRefreshingMore = false
        refreshMoreBean.state = (LoadMoreBean.LoadingMoreState.NONE)
    }

    fun canRefreshMore(): Boolean {
        // 没有更多了不在触发
        if (refreshMoreBean.state === LoadMoreBean.LoadingMoreState.NO_MORE) {
            return false
        }
        if (itemCount == 0 || isRefreshingMore) {
            return false
        }
        return true
    }

    fun isHeaderPosition(position: Int): Boolean {
        val lastPosition = 0
        return hasHeader() && position == lastPosition
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecycler = recyclerView
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is GridLayoutManager) {
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (isHeaderPosition(position)) {
                        layoutManager.spanCount
                    } else 1
                }
            }
        }
    }
}