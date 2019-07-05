package com.redefine.welike.business.publisher.management.handler

import com.redefine.welike.business.publisher.management.BgDispatcher
import com.redefine.welike.business.publisher.management.draft.IPublishDraft
import com.redefine.welike.business.publisher.management.draft.IUploadAttachmentDraft
import com.redefine.welike.business.publisher.management.draft.PicAttachmentDraft
import com.redefine.welike.business.publisher.management.draft.VideoAttachmentDraft
import com.redefine.welike.business.publisher.w
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 *
 * Name: StartHandler
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-07-10 21:25
 *
 */
class ArticleCompressHandler constructor(draft: IPublishDraft, minProgress: Int = 0, maxProgress: Int = 1, next: IHandler?) : AbstractHandler(draft, minProgress, maxProgress, next) {

    override fun start(callback: (IHandler, PublishMessage) -> Unit): Boolean {
        if (!super.start(callback)) {
            return false
        }

        callback(this, PublishMessage(minProgress, PublishMessage.PublishState.RUNNING))

        val articleDraft = draft.getArticleDraft()
        if (articleDraft == null) {
            w("ArticleUploadHandler", "abort because articleDraft = null")
            next(callback)
            return false
        }
        val uploadDraft = articleDraft.getUploadDraft()
        if (uploadDraft == null) {
            w("ArticleUploadHandler", "abort because uploadDraft = null")
            next(callback)
            return false
        }
        when (uploadDraft.getUploadType()) {
            IUploadAttachmentDraft.UploadAttachmentType.PIC -> {
                val picAttachmentDraft = uploadDraft as PicAttachmentDraft
                if (picAttachmentDraft.picList.isEmpty()) {
                    callback(this, PublishMessage(maxProgress, PublishMessage.PublishState.RUNNING))
                    next(callback)
                } else {
                    val doneSignal = CountDownLatch(picAttachmentDraft.picList.size)
                    for (picItemDraft in picAttachmentDraft.picList) {
                        BgDispatcher.dispatch(PicCompressRunnable(doneSignal, picItemDraft))
                    }
                    callback(this, PublishMessage(maxProgress, PublishMessage.PublishState.RUNNING))
                    doneSignal.await(60, TimeUnit.SECONDS)
                    next(callback)
                }
            }
            IUploadAttachmentDraft.UploadAttachmentType.VIDEO -> {
                val videoAttachmentDraft = uploadDraft as VideoAttachmentDraft
                val doneSignal = CountDownLatch(1)
                BgDispatcher.dispatch(VideoCompressRunnable(draft.getDraftId(),doneSignal, videoAttachmentDraft))
                callback(this, PublishMessage(maxProgress, PublishMessage.PublishState.RUNNING))
                doneSignal.await()
                next(callback)
            }
        }
        return true
    }


}