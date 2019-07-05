package com.redefine.welike.commonui.adapter

import android.support.v7.widget.RecyclerView
import android.view.View

/**
 *
 * Name: ViewHolder
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-07-10 14:11
 *
 */

open class KViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun <T> bindForecast(item: T, init: (View, T, Int) -> Unit?) {
        with(item) {
            init(itemView, item, adapterPosition)
        }
    }

    fun <H> bindHeaderForecast(item: H?, header: (View, H?) -> Unit) {

        with(item) {
            header(itemView, item)
        }
    }
}