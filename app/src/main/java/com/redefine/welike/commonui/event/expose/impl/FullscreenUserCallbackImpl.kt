package com.redefine.welike.commonui.event.expose.impl

import android.view.View
import com.redefine.welike.R
import com.redefine.welike.business.user.management.bean.RecommendUser1
import com.redefine.welike.commonui.event.expose.ExposeEventReporter
import com.redefine.welike.commonui.event.expose.base.IDataProvider
import com.redefine.welike.commonui.event.expose.base.IItemVisibleCallback
import com.redefine.welike.commonui.widget.IFollowBtn
import com.redefine.welike.commonui.widget.UserFollowBtn

/**
 * Created by nianguowang on 2019/1/20
 */
class FullscreenUserCallbackImpl: IItemVisibleCallback<List<RecommendUser1>> {
    private lateinit var dataProvider: IDataProvider<List<RecommendUser1>>
    private lateinit var source: String
    private var hasHeader: Boolean = false
    override fun bindDataAndSource(dataProvider: IDataProvider<List<RecommendUser1>>, oldSource: String?) {
        this.dataProvider = dataProvider
        this.source = dataProvider.source
        this.hasHeader = dataProvider.hasHeader()
    }

    override fun updateSource(source: String, oldSource: String?) {
        this.source = source
    }

    override fun onItemVisible(layoutPosition: Int, view: View?, showRatio: Float) {
        view?.let {
            val followBtn = it.findViewById<UserFollowBtn>(R.id.iv_user_follow_followBtn)
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
                ExposeEventReporter.reportFollowBtnExpose(source, user.uid)
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