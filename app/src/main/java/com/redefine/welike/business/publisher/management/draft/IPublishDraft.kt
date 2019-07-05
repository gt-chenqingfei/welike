package com.redefine.welike.business.publisher.management.draft

import com.redefine.richtext.RichContent
import com.redefine.welike.business.publisher.management.bean.DraftBase

/**
 *
 * Name: IPublishDraft
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-07-10 18:10
 *
 */
interface IPublishDraft {
    fun getDraftId(): String
    fun getUploadDraft(): IUploadAttachmentDraft?
    fun getRichContent(): RichContent?
    fun getForwardRichContent(): RichContent?
    fun getArticleDraft(): ArticleDraft?
    fun hasUploadDraft(): Boolean
    fun getDraftSerializable(): DraftBase
}