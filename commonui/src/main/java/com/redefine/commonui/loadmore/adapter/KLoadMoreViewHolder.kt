package com.redefine.commonui.loadmore.adapter

import android.view.View
import com.redefine.commonui.R
import com.redefine.commonui.loadmore.bean.LoadMoreBean
import com.redefine.welike.base.resource.ResourceTool
import com.redefine.welike.commonui.adapter.KViewHolder
import kotlinx.android.synthetic.main.load_more_layout.view.*

/**
 * Name: KLoadMoreViewHolder
 * Author: liwenbo
 * Email:
 * Comment: //loadmore 的viewHolder
 * Date: 2018-07-10 14:27
 */
class KLoadMoreViewHolder(view: View) : KViewHolder(view) {

    private var mLoadingText: String = ""
    private var mNoMoreText: String = ""
    private var mLoadErrorText: String = ""

    init {
        mLoadingText = ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "loading")
        mNoMoreText = ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "no_more")
        mLoadErrorText = ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "load_error")

    }

    fun bindLoadMore(loadMoreBean: LoadMoreBean?, init: (View, LoadMoreBean) -> Unit?) {
        if (loadMoreBean == null || loadMoreBean.state == null) {
            return
        }
        when (loadMoreBean.state!!) {
            LoadMoreBean.LoadingMoreState.NONE -> itemView.load_more_container.visibility = View.GONE
            LoadMoreBean.LoadingMoreState.NO_MORE -> {
                itemView.load_more_container.visibility = View.VISIBLE
                itemView.load_more_progress.visibility = View.INVISIBLE
                itemView.load_more_no_more.visibility = View.VISIBLE
                itemView.load_more_no_more.setImageResource(R.drawable.common_load_more_no_more)
                itemView.load_more_text.text = mNoMoreText
            }
            LoadMoreBean.LoadingMoreState.LOADING -> {
                itemView.load_more_container.visibility = View.VISIBLE
                itemView.load_more_progress.visibility = View.VISIBLE
                itemView.load_more_no_more.visibility = View.INVISIBLE
                itemView.load_more_text.text = mLoadingText
            }
            LoadMoreBean.LoadingMoreState.LOADED_ERROR -> {
                itemView.load_more_container.visibility = View.VISIBLE
                itemView.load_more_progress.visibility = View.INVISIBLE
                itemView.load_more_no_more.visibility = View.VISIBLE
                itemView.load_more_no_more.setImageResource(R.drawable.common_load_more_error)
                itemView.load_more_text.text = mLoadErrorText
            }
        }
        init(itemView, loadMoreBean)
    }
}
