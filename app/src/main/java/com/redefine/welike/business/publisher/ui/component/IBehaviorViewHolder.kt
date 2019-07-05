package com.redefine.welike.business.publisher.ui.component

import android.view.View
import com.redefine.welike.business.publisher.ui.bean.BottomBehaviorItem

/**
 * @author qingfei.chen
 * @date 2019/1/16
 * Copyright (C) 2018 redefine , Inc.
 */
interface IBehaviorViewHolder {
    fun toggleSize(isSmallMode: Boolean, childCount: Int)
    fun bindView(data: BottomBehaviorItem): IBehaviorViewHolder
    fun setEnable(enable: Boolean): IBehaviorViewHolder
    fun setItemSelected(selected: Boolean): IBehaviorViewHolder
    fun getView(): View
}