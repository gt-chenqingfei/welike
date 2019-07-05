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
interface KMultiTypeFactory<T> {

    fun typeOf(t : T): Int
    fun layoutOf(type: Int): Int
    fun holderOf(view: View): KViewHolder


}