package com.redefine.commonui.loadmore.adapter

import android.view.View
import com.redefine.welike.commonui.adapter.KViewHolder

/**
 *
 * Name: KMultiTypeFactory
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-07-10 13:59
 *
 */
interface KMultiTypeLoadMoreFactory<T> {

    fun typeOf(t : T): Int
    fun layoutOf(type: Int): Int
    fun holderOf(view: View): KViewHolder

    fun loadMoreLayoutOf(): Int
    fun loadMoreHolderOf(view: View): KViewHolder
    fun loadMoreTypeOf(): Int
    fun isLoadMoreType(viewType: Int): Boolean
    fun isItem(it: String, t: T): Boolean
    fun headTypeOf(): Int
    fun isHeadType(viewType: Int): Boolean
    fun headLayoutOf(): Int
    fun headHolderOf(view: View): KViewHolder
}