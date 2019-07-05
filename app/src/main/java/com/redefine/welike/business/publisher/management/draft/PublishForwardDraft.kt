package com.redefine.welike.business.publisher.management.draft

import com.redefine.richtext.RichContent
import com.redefine.welike.business.publisher.management.bean.DraftBase
import com.redefine.welike.business.publisher.management.bean.DraftForwardPost

class PublishForwardDraft constructor(
        private val draftId: String,
        var content: RichContent,
        var draft: DraftBase,
        val asComment: Boolean,
        val post: String,
        val forwardPost: String) : IPublishDraft {

    constructor(postDraft: DraftForwardPost) : this(postDraft.draftId, postDraft.content, postDraft,
            postDraft.isAsComment, postDraft.commentPid, postDraft.rootPost.pid)

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
        return null
    }

}
