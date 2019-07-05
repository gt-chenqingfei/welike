package com.redefine.welike.business.publisher.management.draft

import com.redefine.richtext.RichContent
import com.redefine.welike.business.publisher.management.bean.DraftBase
import com.redefine.welike.business.publisher.management.bean.DraftReply
import com.redefine.welike.business.publisher.management.bean.DraftReplyBack

/**
 * Name: PublishPostDraft
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-07-10 17:33
 */

class PublishReplyDraft constructor(
        private val draftId: String,
        val postId: String?,
        val commentId: String,
        val rePost: Boolean,
        val content: RichContent,
        var draft: DraftBase,
        val replyId: String? = null) : IPublishDraft {

    constructor(postDraft: DraftReply) : this(postDraft.draftId, postDraft.pid, postDraft.cid,
            postDraft.isAsRepost, postDraft.content, postDraft)

    constructor(postDraft: DraftReplyBack) : this(postDraft.draftId, postDraft.pid, postDraft.cid,
            postDraft.isAsRepost, postDraft.content, postDraft, postDraft.replyId)

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
        if (draft is DraftReply) {
            return (draft as DraftReply).ccontent
        } else if (draft is DraftReplyBack) {
            return (draft as DraftReplyBack).rcontent
        }

        return null
    }
}

