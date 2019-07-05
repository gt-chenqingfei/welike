package com.redefine.commonui.loadmore.adapter

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.redefine.commonui.loadmore.bean.LoadMoreBean
import com.redefine.foundation.utils.CollectionUtil
import com.redefine.welike.commonui.adapter.KViewHolder

class KMultiTypeLoadMoreAdapter<T>(private val factory: KMultiTypeLoadMoreFactory<T>, private val init: (View, T, Int) -> Unit, private val more: (View, LoadMoreBean) -> Unit?) :
        RecyclerView.Adapter<KViewHolder>() {

    private var isLoadingMore: Boolean = false
    private var loadMoreBean = LoadMoreBean()
    private var mRecycler: RecyclerView? = null
    private var showFooter = false
    private var datas= mutableListOf<T>()



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
            loadMoreViewHolder.bindLoadMore(loadMoreBean, more)
        } else {
            holder.bindForecast(datas[position], init)
        }
    }
     fun addRefreshData(mutableList: MutableList<T>){
         datas.clear()
         if(!CollectionUtil.isEmpty(mutableList)){
             datas.addAll(mutableList)
         }
         notifyDataSetChanged()

     }

    fun addDraftData(mutableListOf: MutableList<T>) {
        if(!CollectionUtil.isEmpty(mutableListOf)){
            datas.addAll(0, mutableListOf)
        }
        notifyDataSetChanged()
    }

    fun addPostDetailRefreshData(mutableList: MutableList<T>){
        datas.clear()
        if(!CollectionUtil.isEmpty(mutableList)){
            datas.addAll(mutableList)

        }
        notifyDataSetChanged()

    }
    fun addProfileCommunityRefreshData(mutableList: MutableList<T>){
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
    fun addGifHisData(mutableList: ArrayList<T>){
        var positionStart=datas.size-1

        if(!CollectionUtil.isEmpty(mutableList)){

            datas.addAll(mutableList)
        }
         notifyItemRangeChanged(positionStart,mutableList.size)
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

    override fun getItemCount(): Int {
        if (CollectionUtil.isEmpty(datas)) {
            return 0
        }
        return if (hasFooter())
            datas.size + 1
        else
            datas.size

    }

    override fun getItemViewType(position: Int): Int {
        return if (isFooterPosition(position))
            factory.loadMoreTypeOf()
        else
            factory.typeOf(datas[position])

    }

    private fun getFooterPosition(): Int {
        return itemCount - 1
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
        if (itemCount == 0 || isLoadingMore) {
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
                if (datas.size == 1) {
                    datas.remove(t)
                    notifyItemRangeRemoved(i, 2)
                } else {
                    datas.remove(t)
                    notifyItemRemoved(i)
                }

                notifyItemRangeChanged(0, datas.size)
                return
            }
        }
    }

    fun deleteByIndex(i: Int) {
        if (i >= 0 && i < datas.size) {
            if (datas.size == 1) {
                datas.removeAt(i)
                notifyItemRangeRemoved(i, 2)
            } else {
                datas.removeAt(i)
                notifyItemRemoved(i)
            }
            notifyItemRangeChanged(0, datas.size)
        }
    }
}