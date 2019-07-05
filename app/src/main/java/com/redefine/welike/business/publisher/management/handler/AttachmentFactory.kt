package com.redefine.welike.business.publisher.management.handler


import com.redefine.richtext.RichContent
import com.redefine.richtext.RichItem
import com.redefine.welike.base.GlobalConfig
import com.redefine.welike.business.publisher.api.bean.*


object AttachmentFactory {

    fun obtainAttachment(it: RichItem): BaseAttachment? {

        when (it.type) {
            AttachmentCategory.TOPIC ->
                return AttachmentTopicBean(it.source, it.display, it.length, it.index)

            AttachmentCategory.LINK ->
                return AttachmentLinkBean(it.source, it.display, it.target, it.length, it.index)

            AttachmentCategory.ARTICLE ->
                return AttachmentArticleBean(it.source, it.display, it.length, it.index)

            AttachmentCategory.SUPER_TOPIC -> {
                var classify = "0"
                it.classify?.let {
                    classify = it
                }

                return AttachmentSuperTopicBean(it.source, it.display, it.id, classify, it.length, it.index)
            }

            AttachmentCategory.MENTION ->
                return AttachmentMentionBean(it.source, it.display, it.length, it.id, it.index)

            else ->
                return null
        }

    }

    fun buildAttachmentsFormRichCotent(content: RichContent?): ArrayList<BaseAttachment?>? {
        var attachments: ArrayList<BaseAttachment?>? = null
        var topicCount = 0
        var superTopicCount = 0
        if (content?.richItemList == null) {
            return null
        }

        if (content.richItemList.size > 0) {
            attachments = ArrayList()
        }

        content.richItemList.forEach {
            if (it.isSuperTopicItem) {
                superTopicCount++
                if (superTopicCount > GlobalConfig.FEED_MAX_SUPER_TOPIC) {
                    return@forEach
                }
            }
            if (it.isTopicItem || it.isSuperTopicItem) {
                topicCount++
                if (topicCount > GlobalConfig.FEED_MAX_TOPIC) {
                    return@forEach
                }
            }
            val attachment = AttachmentFactory.obtainAttachment(it)
            attachments?.add(attachment)
        }

        return attachments
    }
}