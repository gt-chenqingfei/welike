package com.redefine.welike.commonui.event.expose.impl

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import com.redefine.welike.R
import com.redefine.welike.business.feeds.management.bean.PostBase
import com.redefine.welike.commonui.event.expose.ExposeEventReporter
import com.redefine.welike.commonui.event.expose.base.IDataProvider
import com.redefine.welike.commonui.event.expose.base.IItemVisibleCallback
import com.redefine.welike.commonui.widget.IFollowBtn
import com.redefine.welike.commonui.widget.UserFollowBtn
import com.redefine.welike.statistical.EventConstants

/**
 * Created by nianguowang on 2019/1/9
 */
class PostFollowBtnExposeCallbackImpl: IItemVisibleCallback<List<PostBase>> {

    private lateinit var dataProvider: IDataProvider<List<PostBase>>
    private lateinit var source: String
    private var hasHeader: Boolean = false

    override fun bindDataAndSource(dataProvider: IDataProvider<List<PostBase>>, oldSource: String?) {
        this.dataProvider = dataProvider
        source = dataProvider.source
        hasHeader = dataProvider.hasHeader()
    }

    override fun updateSource(source: String, oldSource: String?) {
        this.source = source
    }

    override fun onItemVisible(layoutPosition: Int, view: View?, showRatio: Float) {
        view?.let {
            val followCard = it.findViewById<RecyclerView>(R.id.rv_follow)
            if (followCard != null) {
                var cardSource: String? = null
                if (TextUtils.equals(source, EventConstants.FEED_PAGE_HOME)) {
                    cardSource = EventConstants.FEED_PAGE_HOME_CARD_RECOMMEND
                } else if (TextUtils.equals(source, EventConstants.FEED_PAGE_DISCOVER_FOR_YOU)) {
                    cardSource = EventConstants.FEED_PAGE_DISCOVER_CARD_RECOMMEND
                }
                ExposeEventReporter.reportFollowBtnExpose(cardSource)
                return
            }
            val followBtn = it.findViewById<UserFollowBtn>(R.id.common_feed_follow_btn)
            if (followBtn != null
                    && followBtn.visibility == View.VISIBLE
                    && followBtn.followStatus == IFollowBtn.FollowStatus.FOLLOW) {
                var pos = layoutPosition
                if (hasHeader) {
                    pos--
                }
                if (pos <0 || pos >= dataProvider.data.size) {
                    return
                }
                var postBase = dataProvider.data[pos]
                ExposeEventReporter.reportFollowBtnExpose(source, postBase.uid, postBase.pid, postBase.strategy, postBase.operationType, postBase.language, postBase.tags)
            }
        }
    }

    override fun onItemInvisible(layoutPosition: Int, view: View?) {
    }

    override fun getRatioForExpose(): Float {
        return 0.75F
    }

}