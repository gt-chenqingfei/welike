package com.redefine.welike.commonui.event.expose

import com.redefine.welike.base.track.AFGAEventManager
import com.redefine.welike.base.track.TrackerConstant
import com.redefine.welike.business.feeds.management.bean.ForwardPost
import com.redefine.welike.business.feeds.management.bean.PostBase
import com.redefine.welike.commonui.event.helper.ShareEventHelper
import com.redefine.welike.statistical.EventLog
import com.redefine.welike.statistical.EventLog1

/**
 * Created by nianguowang on 2019/1/10
 */
object ExposeEventReporter {

    fun reportPostExpose(postBase: PostBase?, viewSource: String) {
        if (postBase == null || postBase.pid == null || postBase.uid == null) {
            return
        }
        var rootPostId: String? = null
        var rootPostUid: String? = null
        var rootPostLa: String? = null
        var rootPostTags: Array<String>? = null
        if (postBase is ForwardPost) {
            val rootPost = postBase.rootPost
            if (rootPost != null) {
                rootPostId = rootPost.pid
                rootPostUid = rootPost.uid
                rootPostLa = rootPost.language
                rootPostTags = rootPost.tags
            }
        }
        EventLog1.FeedExpose.report1(postBase.uid, rootPostUid, postBase.pid, rootPostId,
                ShareEventHelper.convertPostType(postBase), viewSource, postBase.strategy, postBase.operationType,
                postBase.language, postBase.tags, rootPostLa, rootPostTags, postBase.sequenceId, postBase.reclogs)
        AFGAEventManager.getInstance().sendAFEvent(TrackerConstant.EVENT_POST_EXP)
    }

    fun reportPostDetailExpose(postBase: PostBase?) {
        if (postBase == null || postBase.pid == null || postBase.uid == null) {
            return
        }
        var rootPostId: String? = null
        var rootPostUid: String? = null
        var rootPostLa: String? = null
        var rootPostTags: Array<String>? = null
        if (postBase is ForwardPost) {
            val rootPost = postBase.rootPost
            if (rootPost != null) {
                rootPostId = rootPost.pid
                rootPostUid = rootPost.uid
                rootPostLa = rootPost.language
                rootPostTags = rootPost.tags
            }
        }
        EventLog1.FeedExpose.report2(postBase.uid, rootPostUid, postBase.pid, rootPostId,
                ShareEventHelper.convertPostType(postBase), postBase.strategy, postBase.operationType,
                postBase.language, postBase.tags, rootPostLa, rootPostTags, postBase.sequenceId, postBase.reclogs)
        AFGAEventManager.getInstance().sendAFEvent(TrackerConstant.EVENT_POST_EXP)
    }

    fun reportPostView(postBase: PostBase?, viewSource: String?, oldSource: String?, viewTime: Long) {
        if (postBase == null || viewTime < 0) {
            return
        }
        var rootPostId: String? = null
        var rootPostUid: String? = null
        var rootPostLa: String? = null
        var rootPostTags: Array<String>? = null
        if (postBase is ForwardPost) {
            val rootPost = postBase.rootPost
            if (rootPost != null) {
                rootPostId = rootPost.pid
                rootPostUid = rootPost.uid
                rootPostLa = rootPost.language
                rootPostTags = rootPost.tags
            }
        }
        EventLog1.FeedView.report1(postBase.uid, rootPostUid, postBase.pid, rootPostId,
                ShareEventHelper.convertPostType(postBase), viewSource,
                viewTime, postBase.strategy,
                postBase.operationType, postBase.language, postBase.tags, rootPostLa, rootPostTags, postBase.sequenceId, postBase.reclogs)
        EventLog.Feed.report9(postBase.uid, rootPostUid, postBase.pid, rootPostId,
                ShareEventHelper.convertPostType(postBase), oldSource,
                viewTime, postBase.strategy,
                postBase.operationType, postBase.language, postBase.tags, rootPostLa, rootPostTags, postBase.sequenceId)
        AFGAEventManager.getInstance().sendAFEvent(TrackerConstant.EVENT_CHECK_POST)
    }

    fun reportPostDetailView(postBase: PostBase?, viewTime: Long) {
        if (postBase == null || viewTime < 0) {
            return
        }
        var rootPostId: String? = null
        var rootPostUid: String? = null
        var rootPostLa: String? = null
        var rootPostTags: Array<String>? = null
        if (postBase is ForwardPost) {
            val rootPost = postBase.rootPost
            if (rootPost != null) {
                rootPostId = rootPost.pid
                rootPostUid = rootPost.uid
                rootPostLa = rootPost.language
                rootPostTags = rootPost.tags
            }
        }
        EventLog1.FeedView.report2(postBase.uid, rootPostUid, postBase.pid, rootPostId,
                ShareEventHelper.convertPostType(postBase), viewTime, postBase.strategy,
                postBase.operationType, postBase.language, postBase.tags, rootPostLa, rootPostTags, postBase.sequenceId, postBase.reclogs)
        EventLog.Feed.report10(postBase.uid, rootPostUid, postBase.pid, rootPostId,
                ShareEventHelper.convertPostType(postBase), viewTime, postBase.strategy,
                postBase.operationType, postBase.language, postBase.tags, rootPostLa, rootPostTags, postBase.sequenceId)
        AFGAEventManager.getInstance().sendAFEvent(TrackerConstant.EVENT_CHECK_POST)
    }

    fun reportPostClick(postBase: PostBase?, viewSource: String?, clickArea: EventLog1.FeedView.FeedClickArea) {
        if (postBase == null) {
            return
        }
        var rootPostId: String? = null
        var rootPostUid: String? = null
        var rootPostLa: String? = null
        var rootPostTags: Array<String>? = null
        if (postBase is ForwardPost) {
            val rootPost = postBase.rootPost
            if (rootPost != null) {
                rootPostId = rootPost.pid
                rootPostUid = rootPost.uid
                rootPostLa = rootPost.language
                rootPostTags = rootPost.tags
            }
        }
        EventLog1.FeedView.report3(postBase.uid, rootPostUid, postBase.pid, rootPostId, ShareEventHelper.convertPostType(postBase),
                viewSource, postBase.strategy, postBase.operationType, postBase.language, postBase.tags, rootPostLa, rootPostTags, clickArea, postBase.sequenceId, postBase.reclogs)
    }

    fun reportFollowBtnExpose(buttonFrom: String? = null, uid: String? = null, postId: String? = null,
                              poolCode: String? = null, operationType: String? = null, postLanguage: String? = null, postTags: Array<String>? = null) {
        EventLog1.Follow.report3(buttonFrom, uid, postId, poolCode, operationType, postLanguage, postTags)
    }
}