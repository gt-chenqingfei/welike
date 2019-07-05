package com.redefine.welike.business.publisher.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.redefine.welike.business.publisher.ui.bean.BottomBehaviorItem
import com.redefine.welike.business.publisher.ui.component.base.BaseLinearContainer


/**
 * @author qingfei.chen
 * @date 2019/1/15
 * Copyright (C) 2018 redefine , Inc.
 */

open abstract class BottomBehaviorView(context: Context?, attrs: AttributeSet?) : BaseLinearContainer(context, attrs),
        OnItemClickListener {

    lateinit var mItems: List<BottomBehaviorItem>
    private lateinit var mHolderMap: HashMap<Int, IBehaviorViewHolder>
    private var listener: OnItemClickListener? = null

    abstract fun getShowItemCountBySmallMode(isSmall: Boolean): Int
    abstract fun lateInitData(): List<BottomBehaviorItem>
    abstract fun newHolderItem(): IBehaviorViewHolder

    override fun onCreateView() {
        mHolderMap = HashMap()
        mItems = lateInitData()
    }

    fun notifyDatasetChanged(isSmall: Boolean) {
        if (mItems == null) {
            return
        }

        mItems.forEach {
            val item = newHolderItem()
            item.bindView(it).toggleSize(isSmall, getShowItemCountBySmallMode(isSmall))
            this.addView(item.getView())
            mHolderMap[it.type] = item
        }
    }

    fun enableItem(item: BottomBehaviorItem) {
        getItem(item.type)?.setEnable(item.enabled)
    }

    fun getItem(item: Int): IBehaviorViewHolder? {
        return mHolderMap[item]
    }

    fun toggleSize(isSmall: Boolean) {
        mHolderMap.forEach {
            it.value.toggleSize(isSmall, getShowItemCountBySmallMode(isSmall))
        }
    }

    override fun onItemClick(item: BottomBehaviorItem?) {
        listener?.let {
            it.onItemClick(item)
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun setItemSelected(isSelected: Boolean,type:Int) {
        val emoji = getItem(type)
        if (emoji != null) {
            emoji.setItemSelected(isSelected)
        }
    }

    fun getEmojiView(): View? {
        return getItem(BottomBehaviorItem.BEHAVIOR_EMOJI_TYPE)?.getView()
    }

}