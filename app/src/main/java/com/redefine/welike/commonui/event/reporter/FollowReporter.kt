package com.redefine.welike.commonui.event.reporter

import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.commonui.event.expose.model.FollowEventModel
import com.redefine.welike.statistical.EventLog1

/**
 * Created by nianguowang on 2019/1/21
 */
class FollowReporter {

    companion object {
        fun reportFollow(eventModel: FollowEventModel, toUid: String) {
            val postBase = eventModel.postBase
            AccountManager.getInstance().account?.let {
                if (postBase == null) {
                    EventLog1.Follow.report1(eventModel.source, it.uid, toUid, null, null, null, null, null, null, null, null)
                } else {
                    EventLog1.Follow.report1(eventModel.source, it.uid, toUid, null, postBase.pid, postBase.strategy, postBase.operationType, postBase.language, postBase.tags, postBase.sequenceId, postBase.reclogs)
                }
            }
        }

        fun reportUnFollow(eventModel: FollowEventModel, toUid: String) {
            val postBase = eventModel.postBase
            AccountManager.getInstance().account?.let {
                if (postBase == null) {
                    EventLog1.Follow.report2(eventModel.source, it.uid, toUid, null, null, null, null, null, null, null)
                } else {
                    EventLog1.Follow.report2(eventModel.source, it.uid, toUid, null, postBase.pid, postBase.strategy, postBase.operationType, postBase.language, postBase.tags, postBase.reclogs)
                }
            }
        }
    }
}