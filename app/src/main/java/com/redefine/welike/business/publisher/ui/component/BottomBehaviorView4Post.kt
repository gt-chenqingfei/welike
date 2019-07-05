package com.redefine.welike.business.publisher.ui.component

import android.content.Context
import android.util.AttributeSet
import com.redefine.welike.business.publisher.ui.activity.BottomBehaviorDataProvider
import com.redefine.welike.business.publisher.ui.bean.BottomBehaviorItem
import com.redefine.welike.business.publisher.ui.viewholder.BottomBehaviorVH


/**
 * @author qingfei.chen
 * @date 2019/1/15
 * Copyright (C) 2018 redefine , Inc.
 */

class BottomBehaviorView4Post(context: Context?, attrs: AttributeSet?) : BottomBehaviorView(context, attrs),
        OnItemClickListener {

    override fun newHolderItem(): IBehaviorViewHolder {
        return BottomBehaviorVH(context, this)
    }

    override fun lateInitData(): List<BottomBehaviorItem> {
        return BottomBehaviorDataProvider.getPostBehaviorData()
    }

    override fun getShowItemCountBySmallMode(isSmall: Boolean): Int {
        return if (isSmall) 8 else 5
    }

    override fun onCreateView() {
        super.onCreateView()
        notifyDatasetChanged(false)
    }

    fun setAllEnabled(enabled: Boolean) {
        for (item in mItems) {
            item.enabled = enabled
            enableItem(item)
        }
    }

    fun setVideoStatusPollDisable() {
        getItem(BottomBehaviorItem.BEHAVIOR_VIDEO_ITEM_TYPE)?.setEnable(false)
        getItem(BottomBehaviorItem.BEHAVIOR_EASY_POST)?.setEnable(false)
        getItem(BottomBehaviorItem.BEHAVIOR_POLL_ITEM_TYPE)?.setEnable(false)
    }

    fun setVideoCameraStatusPollDisable() {
        getItem(BottomBehaviorItem.BEHAVIOR_EASY_POST)?.setEnable(false)
        getItem(BottomBehaviorItem.BEHAVIOR_POLL_ITEM_TYPE)?.setEnable(false)
        getItem(BottomBehaviorItem.BEHAVIOR_SNAPSHOT_ITEM_TYPE)?.setEnable(false)
    }

    fun setAlbumVideoCameraStatusPollDisable() {
        getItem(BottomBehaviorItem.BEHAVIOR_VIDEO_ITEM_TYPE)?.setEnable(false)
        getItem(BottomBehaviorItem.BEHAVIOR_EASY_POST)?.setEnable(false)
        getItem(BottomBehaviorItem.BEHAVIOR_POLL_ITEM_TYPE)?.setEnable(false)
        getItem(BottomBehaviorItem.BEHAVIOR_SNAPSHOT_ITEM_TYPE)?.setEnable(false)
        getItem(BottomBehaviorItem.BEHAVIOR_ALUMB_ITEM_TYPE)?.setEnable(false)
    }

    fun setAlbumVideoCameraPollDisable() {
        getItem(BottomBehaviorItem.BEHAVIOR_VIDEO_ITEM_TYPE)?.setEnable(false)
        getItem(BottomBehaviorItem.BEHAVIOR_POLL_ITEM_TYPE)?.setEnable(false)
        getItem(BottomBehaviorItem.BEHAVIOR_SNAPSHOT_ITEM_TYPE)?.setEnable(false)
        getItem(BottomBehaviorItem.BEHAVIOR_ALUMB_ITEM_TYPE)?.setEnable(false)
    }

}