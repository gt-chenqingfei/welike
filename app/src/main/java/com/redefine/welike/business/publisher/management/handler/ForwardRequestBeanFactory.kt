package com.redefine.welike.business.publisher.management.handler


import com.redefine.foundation.utils.CommonHelper
import com.redefine.welike.MyApplication
import com.redefine.welike.base.SpManager
import com.redefine.welike.business.publisher.api.bean.CommentBean
import com.redefine.welike.business.publisher.api.bean.PostBean
import com.redefine.welike.business.publisher.api.bean.RequestForwardBean
import com.redefine.welike.business.publisher.management.draft.PublishForwardDraft


object ForwardRequestBeanFactory {

    fun buildPublishBean(postDraft: PublishForwardDraft): RequestForwardBean {

        var source: String? = null
        if (!SpManager.Setting.getCurrentMobileModelSetting(MyApplication.getAppContext())) {
            source = CommonHelper.getDeviceModel()
        }

        val attachments = AttachmentFactory.buildAttachmentsFormRichCotent(postDraft.getRichContent())
        val content = postDraft.getRichContent()?.text ?: ""
        val summary = postDraft.getRichContent()?.summary ?: ""

        val postBean = PostBean(content, summary, attachments, source = source, forwardPost = postDraft.forwardPost)

        var commentBean: CommentBean? = null
        if (postDraft.asComment) {
            commentBean = CommentBean(postDraft.post, content, attachments)
        }
        return RequestForwardBean(postBean, commentBean)
    }


}