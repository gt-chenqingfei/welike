package com.redefine.commonui.loadmore.adapter;

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.redefine.foundation.utils.CollectionUtil
import com.redefine.welike.commonui.adapter.KViewHolder

class KAdapter<T>(private val layoutResourceId: Int, private val init: (View, T, Int) -> Unit) :
        RecyclerView.Adapter<KViewHolder>() {
    private var datas = mutableListOf<T>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutResourceId, parent, false)
        return KViewHolder(view)
    }

    override fun onBindViewHolder(holder: KViewHolder, position: Int) {
        if (position < datas.size && position > -1) {
            holder.bindForecast(datas[position], init)
        }
    }

    override fun getItemCount() = datas.size

    fun addData(mutableList: MutableList<T>) {
        if (CollectionUtil.isEmpty(mutableList)) {
            return
        }
        datas.addAll(mutableList)
        notifyDataSetChanged()
    }

    fun setData(mutableList: MutableList<T>) {
        datas.clear()
        if (!CollectionUtil.isEmpty(mutableList)) {
            datas.addAll(mutableList)
        }
        notifyDataSetChanged()
    }

    fun clearData() {
        datas.clear()
        notifyDataSetChanged()
    }

    fun getData(): MutableList<T> {
        return datas
    }
}