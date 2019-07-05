package com.redefine.welike.business.feeds.ui.listener

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.redefine.welike.business.feeds.ui.adapter.FeedItemFollowAdapter
import com.redefine.welike.business.feeds.ui.viewholder.FeedFollowViewHolder
import com.redefine.welike.statistical.EventConstants
import com.redefine.welike.statistical.manager.InterestAndRecommendCardEventManager

/**
 * Created by nianguowang on 2019/1/15
 */
class DefaultFollowClickListener(var recyclerView: RecyclerView, var source: String, var listener: FeedFollowViewHolder.OnFollowChangeListener? = null): FeedItemFollowAdapter.OnAdapterClickListener {

    override fun onItemUnFollow(uid: String) {
    }

    override fun onItemFollow(position: Int) {
        val targetPos = position + 1
        val lastVisibleItemPosition = (recyclerView.getLayoutManager() as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
        recyclerView.scrollToPosition(if (targetPos == lastVisibleItemPosition) targetPos + 1 else targetPos)
    }

    override fun onDelete(size: Int) {
        InterestAndRecommendCardEventManager.INSTANCE.setButton_type(EventConstants.INTEREST_CARD_BUTTON_TYPE_CANCEL)
        InterestAndRecommendCardEventManager.INSTANCE.report7()
        if (size == 0) {
            listener?.let { it.onCancel() }
        }
    }
}