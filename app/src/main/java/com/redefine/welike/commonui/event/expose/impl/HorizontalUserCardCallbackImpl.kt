package com.redefine.welike.commonui.event.expose.impl

import android.view.View
import com.redefine.welike.R
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.business.user.management.bean.RecommendSlideUser
import com.redefine.welike.business.user.management.bean.RecommendUser
import com.redefine.welike.commonui.event.expose.ExposeEventReporter
import com.redefine.welike.commonui.event.expose.base.IDataProvider
import com.redefine.welike.commonui.event.expose.base.IItemVisibleCallback
import com.redefine.welike.commonui.widget.IFollowBtn
import com.redefine.welike.commonui.widget.UserFollowBtn
import com.redefine.welike.statistical.EventLog1

/**
 * Created by nianguowang on 2019/1/20
 */
class HorizontalUserCardCallbackImpl: IItemVisibleCallback<List<RecommendSlideUser>> {
    private lateinit var dataProvider: IDataProvider<List<RecommendSlideUser>>
    private lateinit var source: String
    private var hasHeader: Boolean = false

    override fun bindDataAndSource(dataProvider: IDataProvider<List<RecommendSlideUser>>, oldSource: String?) {
        this.dataProvider = dataProvider
        this.source = dataProvider.source
        this.hasHeader = dataProvider.hasHeader()
    }

    override fun updateSource(source: String, oldSource: String?) {
        this.source = source
    }

    override fun onItemVisible(layoutPosition: Int, view: View?, showRatio: Float) {
        view?.let {
            val followBtn = it.findViewById<UserFollowBtn>(R.id.common_feed_follow_btn)
            if (followBtn != null
                    && followBtn.visibility == View.VISIBLE
                    && followBtn.followStatus == IFollowBtn.FollowStatus.FOLLOW) {
                var pos = layoutPosition
                if (hasHeader) {
                    pos--
                }
                if (pos < 0 || pos >= dataProvider.data.size) {
                    return
                }
                val user = dataProvider.data[pos]
                AccountManager.getInstance().account?.let {
                    EventLog1.Follow.report6(source, it.uid, user.uid)
                }

            }
        }
    }

    override fun onItemInvisible(layoutPosition: Int, view: View?) {
        //do nothing
    }

    override fun getRatioForExpose(): Float {
        return 0.5F
    }
}