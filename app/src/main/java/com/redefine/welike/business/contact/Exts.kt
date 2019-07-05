package com.redefine.welike.business.contact

import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.commonui.widget.IFollowBtn
import com.redefine.welike.commonui.widget.UserFollowBtn

fun IFollowBtn.getState(uid: String, follow: Boolean, followed: Boolean): IFollowBtn.FollowStatus {
    return when {
        AccountManager.getInstance().isSelf(uid) -> IFollowBtn.FollowStatus.GONE
        else -> if (follow && followed) {
            IFollowBtn.FollowStatus.FRIEND
        } else if (followed) {
            IFollowBtn.FollowStatus.FOLLOW
        } else if (follow) {
            IFollowBtn.FollowStatus.FOLLOWING
        } else {
            IFollowBtn.FollowStatus.FOLLOW
        }
    }
}
