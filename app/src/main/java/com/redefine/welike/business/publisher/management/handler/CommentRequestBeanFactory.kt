package com.redefine.welike.business.publisher.management.handler


import com.redefine.foundation.utils.CommonHelper
import com.redefine.welike.MyApplication
import com.redefine.welike.base.SpManager
import com.redefine.welike.business.publisher.api.bean.BaseAttachment
import com.redefine.welike.business.publisher.api.bean.CommentBean
import com.redefine.welike.business.publisher.api.bean.PostBean
import com.redefine.welike.business.publisher.api.bean.RequestCommentBean
import com.redefine.welike.business.publisher.management.draft.PublishCommentDraft


object CommentRequestBeanFactory {

    fun buildPublishBean(postDraft: PublishCommentDraft): RequestCommentBean {

        var source: String? = null
        if (!SpManager.Setting.getCurrentMobileModelSetting(MyApplication.getAppContext())) {
            source = CommonHelper.getDeviceModel()
        }

        var postBean: PostBean? = null
        if (postDraft.rePost) {
            val rePostAttachments: ArrayList<BaseAttachment?>? =
                    AttachmentFactory.buildAttachmentsFormRichCotent(postDraft.getForwardRichContent())
            val forwardContent = postDraft.getForwardRichContent()?.text ?: ""
            val forwardSummary = postDraft.getForwardRichContent()?.text ?: ""
            postBean = PostBean(forwardContent, forwardSummary, rePostAttachments, source = source, forwardPost = postDraft.postId)
        }


        val commentAttachments = AttachmentFactory.buildAttachmentsFormRichCotent(postDraft.getRichContent())
        val commentContent = postDraft.getRichContent()?.text ?: ""
        val commentBean = CommentBean(postDraft.postId, commentContent, commentAttachments)
        return RequestCommentBean(commentBean, postBean)
    }


}