package com.redefine.welike.business.publisher.management.handler


import com.redefine.foundation.utils.CommonHelper
import com.redefine.welike.MyApplication
import com.redefine.welike.base.SpManager
import com.redefine.welike.business.publisher.api.bean.PostBean
import com.redefine.welike.business.publisher.api.bean.ReplayBean
import com.redefine.welike.business.publisher.api.bean.RequestReplyBean
import com.redefine.welike.business.publisher.management.draft.PublishReplyDraft


object ReplyRequestBeanFactory {

    fun buildPublishBean(postDraft: PublishReplyDraft): RequestReplyBean {

        var source: String? = null
        if (!SpManager.Setting.getCurrentMobileModelSetting(MyApplication.getAppContext())) {
            source = CommonHelper.getDeviceModel()
        }

        var postBean: PostBean? = null
        if (postDraft.rePost) {

            val rePostAttachments = AttachmentFactory.buildAttachmentsFormRichCotent(postDraft.getForwardRichContent())
            val rePostContent = postDraft.getForwardRichContent()?.text ?: ""
            val rePostSummary = postDraft.getForwardRichContent()?.text ?: ""
            postBean = PostBean(rePostContent, rePostSummary, rePostAttachments, source = source, forwardPost = postDraft.postId)
        }

        val replyAttachments = AttachmentFactory.buildAttachmentsFormRichCotent(postDraft.getRichContent())
        val replyContent = postDraft.getRichContent()?.text ?: ""
        val replyBean = ReplayBean(postDraft.commentId, postDraft.replyId, replyContent, replyAttachments)
        return RequestReplyBean(replyBean, postBean)
    }


}