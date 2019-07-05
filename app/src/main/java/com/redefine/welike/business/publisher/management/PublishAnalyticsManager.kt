package com.redefine.welike.business.publisher.management

import android.text.TextUtils
import com.redefine.foundation.utils.CommonHelper
import com.redefine.welike.business.publisher.api.bean.*
import com.redefine.welike.commonui.event.model.PublishEventModel
import com.redefine.welike.statistical.EventLog1

/**
 * @author qingfei.chen
 * @date 2018/12/3
 * Copyright (C) 2018 redefine , Inc.
 */
class PublishAnalyticsManager {
    private val mEventModelCache: HashMap<String, PublishEventModel> = HashMap()

    var mCurrentPublishDraftId: String? = null

    companion object {
        const val TAG: String = "PublishAnalyticsManager"
        fun getInstance() = Holder.INSTANCE
    }

    private object Holder {
        val INSTANCE = PublishAnalyticsManager()
    }

    fun obtainEventModel(draftId: String?): PublishEventModel {
        var m = mEventModelCache[draftId]
        if (m == null) {
            var draftIdTmp = draftId
            if (TextUtils.isEmpty(draftIdTmp)) {
                draftIdTmp = CommonHelper.generateUUID()
            }
            mCurrentPublishDraftId = draftIdTmp

            m = PublishEventModel(EventLog1.Publish.Source.MAIN_PAGE, EventLog1.Publish.MainSource.OTHER)
            mEventModelCache[draftIdTmp!!] = m
        }
        return m
    }

    fun obtainCurrentModel(): PublishEventModel {
        return obtainEventModel(mCurrentPublishDraftId)
    }

    private fun generateEventModel(draftId: String, source: EventLog1.Publish.Source?, mainSource: EventLog1.Publish.MainSource?, repostId: String?): PublishEventModel {
        var m = mEventModelCache[draftId]
        if (m == null) {
            m = PublishEventModel(source, mainSource, repostId)
            mEventModelCache[draftId] = m
            mEventModelCache[draftId]?.draftId = draftId
            mCurrentPublishDraftId = draftId
        }
        return m
    }

    fun generateEventModel(draftId: String): PublishEventModel {
        return generateEventModel(draftId, null, null, null)
    }

    fun commentAnalyticsComplete(comment: ResponseCommentBean?, draftId: String) {
        comment ?: return
        try {
            val m = obtainEventModel(draftId)
            m?.let {
                m.postId = comment.comment.post.id
//                m.contentUid = comment.comment.user?.id
                m.postLa = comment.post?.forwardPost?.language
                m.postTags = comment.post?.forwardPost?.tags
                setTopicId(m, comment.comment.attachments)
                m.proxy.report15()
                mEventModelCache.remove(draftId)
            }
        } catch (e: Exception) {
            com.redefine.welike.business.publisher.e(TAG, e.toString())
        }
    }


    fun setRetryTimes(retryTimes: Int, draftId: String) {

        try {
            val m = obtainEventModel(draftId)
            m?.let {
                m.retryTimes = retryTimes
            }
        } catch (e: Exception) {
            com.redefine.welike.business.publisher.e(TAG, e.toString())
        }
    }

    fun postAnalyticsComplete(post: ResponsePostBean?, draftId: String) {
        post ?: return
        try {
            val m = obtainEventModel(draftId)
            m?.let {
                m.postId = post.post.id
                setTopicId(m, post.post.attachments)
                post.post.community?.let { community ->
                    m.community = community.id
                }
//                m.contentUid = post.post.user?.id
                m.proxy.report15()
                mEventModelCache.remove(draftId)
            }
        } catch (e: Exception) {
            com.redefine.welike.business.publisher.e(TAG, e.toString())
        }
    }

    fun forwardAnalyticsComplete(forward: ResponseForwardBean?, draftId: String) {
        forward ?: return
        try {
            val m = obtainEventModel(draftId)
            m?.let {
                m.postId = forward.post.id
                m.repostId = forward.post.forwardPost?.id
                m.postLa = forward.post.forwardPost?.language
                m.postTags = forward.post.forwardPost?.tags
//                m.contentUid = forward.post.forwardPost?.user?.id
                setTopicId(m, forward.post.attachments)
                m.proxy.report15()
                mEventModelCache.remove(draftId)
            }
        } catch (e: Exception) {
            com.redefine.welike.business.publisher.e(TAG, e.toString())
        }
    }

    fun replyAnalyticsComplete(reply: ResponseReplyBean?, draftId: String) {
        reply ?: return
        try {
            val m = obtainEventModel(draftId)
            m?.let {
                m.postId = reply.reply.comment.post.id
                m.postLa = reply.post?.forwardPost?.language
                m.postTags = reply.post?.forwardPost?.tags
//                m.contentUid = reply.reply.user?.id
                setTopicId(m, reply.reply.attachments)
                m.proxy.report15()
                mEventModelCache.remove(draftId)
            }
        } catch (e: Exception) {
            com.redefine.welike.business.publisher.e(TAG, e.toString())
        }
    }

    fun publishErrorAnalytics(draftId: String, errorCode: Int, errorMessage: String?) {
        try {
            val m = obtainEventModel(draftId)

            m?.let {
                m.errorCode = errorCode
                m.errorMessage = errorMessage
                m.proxy.report22()
            }
        } catch (e: Exception) {
            com.redefine.welike.business.publisher.e(TAG, e.toString())
        }
    }

    private fun setTopicId(m: PublishEventModel, attachments: ArrayList<Attachment>?) {
        attachments?.let {
            val builder = StringBuilder()
            var i = 0
            var count = 0
            it.forEach { attachment ->
                i++
                if (attachment.type == AttachmentCategory.TOPIC ||
                        attachment.type == AttachmentCategory.SUPER_TOPIC) {
                    builder.append(attachment.id)
                    count++
                    if (i <= attachments.size - 1) {
                        builder.append(",")
                    }
                }
            }

            m.topicId = builder.toString()
            m.topicNum = count
        }
    }

}