package com.redefine.commonui.loadmore.adapter

import android.view.View
import com.redefine.commonui.R
import com.redefine.welike.commonui.adapter.KViewHolder

/**
 *
 * Name: KCommonLoadMoreFactory
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-07-10 15:35
 *
 */

abstract class KCommonLoadMoreFactory<T> : KMultiTypeLoadMoreFactory<T> {

    companion object {
        const val FOOTER_VIEW_TYPE: Int = -200
        const val HEADER_VIEW_TYPE: Int = -100
    }

    override fun isItem(it: String, t: T): Boolean {
        return false
    }

    override fun holderOf(view: View): KViewHolder {
        return KViewHolder(view)
    }

    override fun loadMoreLayoutOf(): Int = R.layout.load_more_layout

    override fun loadMoreHolderOf(view: View): KViewHolder {
        return KLoadMoreViewHolder(view)
    }

    override fun loadMoreTypeOf(): Int {
        return FOOTER_VIEW_TYPE
    }

    override fun isLoadMoreType(viewType: Int): Boolean {
        return viewType == FOOTER_VIEW_TYPE
    }

    override fun headTypeOf(): Int {
        return HEADER_VIEW_TYPE
    }

    override fun headHolderOf(view: View): KViewHolder {
        return KViewHolder(view)
    }

    override fun isHeadType(viewType: Int): Boolean {
        return viewType == HEADER_VIEW_TYPE
    }

    override fun headLayoutOf(): Int {
        return R.layout.head_layout
    }
}