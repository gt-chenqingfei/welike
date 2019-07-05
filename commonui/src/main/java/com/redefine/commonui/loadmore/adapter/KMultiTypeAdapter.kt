package com.redefine.commonui.loadmore.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.redefine.welike.commonui.adapter.KViewHolder

class KMultiTypeAdapter<T>(private val factory: KMultiTypeFactory<T>, private val items: List<T>, private val init: (View, T, Int) -> Unit) :
        RecyclerView.Adapter<KViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(factory.layoutOf(viewType), parent, false)
        return factory.holderOf(view)
    }

    override fun onBindViewHolder(holder: KViewHolder, position: Int) {
        holder.bindForecast(items[position], init)
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int): Int {
        return factory.typeOf(items[position])
    }
}