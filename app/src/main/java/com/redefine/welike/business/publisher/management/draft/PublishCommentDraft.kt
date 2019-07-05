package com.redefine.welike.business.publisher.management.draft

import com.redefine.richtext.RichContent
import com.redefine.welike.business.publisher.management.bean.DraftBase
import com.redefine.welike.business.publisher.management.bean.DraftComment

class PublishCommentDraft constructor(
        private val draftId: String,
        val postId: String,
        val rePost: Boolean,
        val content: RichContent,
        var draft: DraftBase) : IPublishDraft {

    constructor(postDraft: DraftComment) : this(postDraft.draftId, postDraft.pid, postDraft.isAsRepost, postDraft.content, postDraft)

    override fun getDraftSerializable(): DraftBase {
        return draft
    }

    override fun getUploadDraft(): IUploadAttachmentDraft? {
        return null
    }

    override fun getArticleDraft(): ArticleDraft? {
        return null
    }

    override fun getDraftId(): String {
        return draftId
    }

    override fun getRichContent(): RichContent? {
        return content
    }

    override fun hasUploadDraft(): Boolean {
        return false
    }

    override fun getForwardRichContent(): RichContent? {
        return (draft as DraftComment).fcontent
    }

}
