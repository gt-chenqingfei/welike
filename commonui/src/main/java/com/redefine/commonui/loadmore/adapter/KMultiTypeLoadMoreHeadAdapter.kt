package com.redefine.commonui.loadmore.adapter

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.redefine.commonui.loadmore.bean.LoadMoreBean
import com.redefine.foundation.utils.CollectionUtil
import com.redefine.welike.commonui.adapter.KViewHolder

class KMultiTypeLoadMoreHeadAdapter<T, H>(private val factory: KMultiTypeLoadMoreFactory<T>
                                       , private val init: (View, T, Int) -> Unit
                                       , private val more: (View, LoadMoreBean) -> Unit
                                       , private val header: (View, H?) -> Unit = { _: View, _: H? -> }) :
        RecyclerView.Adapter<KViewHolder>() {

    private var isLoadingMore: Boolean = false
    private var loadMoreBean = LoadMoreBean()
    private var headerBean: H? = null
    private var mRecycler: RecyclerView? = null
    private var showFooter = false
    private var showHeader = false
    private var datas= mutableListOf<T>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KViewHolder {
        if (factory.isLoadMoreType(viewType)) {
            val view = LayoutInflater.from(parent.context).inflate(factory.loadMoreLayoutOf(), parent, false)
            return factory.loadMoreHolderOf(view)
        }
        if (factory.isHeadType(viewType)) {
            val view = LayoutInflater.from(parent.context).inflate(factory.headLayoutOf(), parent, false)
            return factory.headHolderOf(view)
        }
        val view = LayoutInflater.from(parent.context).inflate(factory.layoutOf(viewType), parent, false)
        return factory.holderOf(view)
    }

    override fun onBindViewHolder(holder: KViewHolder, position: Int) {
        when {
            factory.isLoadMoreType(getItemViewType(position)) -> {
                val loadMoreViewHolder: KLoadMoreViewHolder = holder as KLoadMoreViewHolder
                loadMoreViewHolder.bindLoadMore(loadMoreBean, more)
            }
            factory.isHeadType(getItemViewType(position)) -> holder.bindHeaderForecast(headerBean, header)
            else -> {
                val p = if (hasHeader()){
                    position - 1
                } else {
                    position
                }
                holder.bindForecast(datas[p], init)
            }
        }
    }
     fun addRefreshData(mutableList: MutableList<T>){
         datas.clear()
         if(!CollectionUtil.isEmpty(mutableList)){
             datas.addAll(mutableList)
         }
         notifyDataSetChanged()

     }

    fun addHisData(mutableList: MutableList<T>){
        if(!CollectionUtil.isEmpty(mutableList)){
            datas.addAll(mutableList)

        }
        notifyDataSetChanged()

    }

    /**
     * Call this method to show hiding footer.
     */
    fun showFooter() {
        this.showFooter = true
        notifyItemChanged(getFooterPosition())
    }

    /**
     * Returns true if the footer configured is not null.
     */
    private fun hasFooter(): Boolean {
        return showFooter
    }

    /**
     * Call this method to hide footer.
     */
    fun hideFooter() {
        this.showFooter = false
        notifyItemChanged(getFooterPosition())
    }

    /**
     * Call this method to show hiding footer.
     */
    fun showHeader() {
        this.showHeader = true
        notifyItemChanged(getHeaderPosition())
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
        notifyItemChanged(getHeaderPosition())
    }

    override fun getItemCount(): Int {
        var count = CollectionUtil.getCount(datas)
        if (count != 0) {
            if (hasFooter()) {
                count++
            }
        }
        if (hasHeader()) {
            count++
        }
        return count
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isFooterPosition(position) -> factory.loadMoreTypeOf()
            isHeaderPosition(position) -> factory.headTypeOf()
            else -> {
                val p = if (hasHeader()){
                    position - 1
                } else {
                    position
                }
                factory.typeOf(datas[p])
            }
        }

    }

    private fun isHeaderPosition(position: Int): Boolean {
        return hasHeader() && position == 0
    }

    private fun getFooterPosition(): Int {
        return itemCount - 1
    }

    private fun getHeaderPosition(): Int {
        return 0
    }

    fun onLoadMore() {
        showFooter = true
        isLoadingMore = true
        loadMoreBean.state = LoadMoreBean.LoadingMoreState.LOADING
        notifyItemChanged(getFooterPosition())
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        mRecycler = null
        super.onDetachedFromRecyclerView(recyclerView)
    }

    fun disableLoadMore() {
        showFooter = true
        isLoadingMore = true
        loadMoreBean.state = (LoadMoreBean.LoadingMoreState.NONE)
        notifyItemChanged(getFooterPosition())
    }

    fun finishLoadMore() {
        showFooter = true
        isLoadingMore = false
        loadMoreBean.state = (LoadMoreBean.LoadingMoreState.LOADING)
        notifyItemChanged(getFooterPosition())
    }

    fun loadError() {
        showFooter = true
        isLoadingMore = false
        loadMoreBean.state = (LoadMoreBean.LoadingMoreState.LOADED_ERROR)
        notifyItemChanged(getFooterPosition())
    }

    fun noMore() {
        showFooter = true
        isLoadingMore = false
        loadMoreBean.state = (LoadMoreBean.LoadingMoreState.NO_MORE)
        notifyItemChanged(getFooterPosition())
    }

    fun clearFinishFlag() {
        showFooter = true
        isLoadingMore = false
        loadMoreBean.state = (LoadMoreBean.LoadingMoreState.NONE)
        notifyItemChanged(getFooterPosition())
    }

    fun goneLoadMore() {
        showFooter = true
        isLoadingMore = false
        loadMoreBean.state = (LoadMoreBean.LoadingMoreState.NONE)
    }

    fun canLoadMore(): Boolean {
        // 没有更多了不在触发
        if (!hasFooter()) {
            return false
        }
        if (loadMoreBean.state === LoadMoreBean.LoadingMoreState.NO_MORE) {
            return false
        }
        if (datas.size == 0 || isLoadingMore) {
            return false
        }
        return true
    }

    fun isFooterPosition(position: Int): Boolean {
        val lastPosition = itemCount - 1
        return hasFooter() && position == lastPosition
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecycler = recyclerView
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is GridLayoutManager) {
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (isFooterPosition(position)) {
                        layoutManager.spanCount
                    } else 1
                }
            }
        }
    }

    fun delete(it: String) {
        datas.forEachIndexed { i: Int, t: T ->
            if (factory.isItem(it, t)) {
                val position = if (hasHeader()) i + 1 else i
                if (datas.size == 1) {
                    datas.remove(t)
                    notifyItemRangeRemoved(position, 2)
                } else {
                    datas.remove(t)
                    notifyItemRemoved(position)
                }

                notifyItemRangeChanged(0, datas.size)
                return
            }
        }
    }

    fun deleteByIndex(i: Int) {
        var position = i
        if (hasHeader()) {
            position = i - 1
        }
        if (position >= 0 && position < datas.size) {
            if (datas.size == 1) {
                datas.removeAt(position)
                notifyItemRangeRemoved(i, 2)
            } else {
                datas.removeAt(position)
                notifyItemRemoved(i)
            }
            notifyItemRangeChanged(0, datas.size)
        }
    }
}