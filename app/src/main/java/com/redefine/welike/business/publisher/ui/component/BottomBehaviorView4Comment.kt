package com.redefine.welike.business.publisher.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.redefine.welike.business.publisher.ui.activity.BottomBehaviorDataProvider
import com.redefine.welike.business.publisher.ui.bean.BottomBehaviorItem
import com.redefine.welike.business.publisher.ui.viewholder.BottomBehaviorVH1


/**
 * @author qingfei.chen
 * @date 2019/1/15
 * Copyright (C) 2018 redefine , Inc.
 */

class BottomBehaviorView4Comment(context: Context?, attrs: AttributeSet?) :
        BottomBehaviorView(context, attrs),
        OnItemClickListener {
    override fun newHolderItem(): IBehaviorViewHolder {
        return BottomBehaviorVH1(context, this)
    }

    override fun lateInitData(): List<BottomBehaviorItem> {
        return BottomBehaviorDataProvider.getCommentBehaviorData()
    }

    override fun getShowItemCountBySmallMode(isSmall: Boolean): Int {
        return 4
    }

    override fun onCreateView() {
        super.onCreateView()
        notifyDatasetChanged(true)
    }

    fun setAllEnabled(enabled: Boolean) {
        for (item in mItems) {
            item.enabled = enabled
            enableItem(item)
        }
    }

    fun getTopicView(): View? {
        return getItem(BottomBehaviorItem.BEHAVIOR_TOPIC_ITEM_TYPE)?.getView()
    }

}