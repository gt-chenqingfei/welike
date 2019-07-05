package com.redefine.welike.business.publisher.management.handler

import android.text.TextUtils
import com.redefine.welike.business.publisher.management.BgDispatcher
import com.redefine.welike.business.publisher.management.draft.IPublishDraft
import com.redefine.welike.business.publisher.management.draft.IUploadAttachmentDraft
import com.redefine.welike.business.publisher.management.draft.PicAttachmentDraft
import com.redefine.welike.business.publisher.management.draft.VideoAttachmentDraft
import com.redefine.welike.business.publisher.w
import java.util.concurrent.CountDownLatch

/**
 *
 * Name: UploadHandler
 * Author: liwenbo
 * Email:
 * Comment: //负责发布任务上传的责任链
 * Date: 2018-07-11 00:53
 *
 */

class ArticleUploadHandler constructor(draft: IPublishDraft, minProgress: Int = 0, maxProgress: Int = 1, next: IHandler?) : AbstractHandler(draft, minProgress, maxProgress, next) {


    override fun start(callback: (IHandler, PublishMessage) -> Unit): Boolean {
        if (!super.start(callback)) {
            return false
        }
        callback(this, PublishMessage(minProgress, PublishMessage.PublishState.RUNNING))
        doUpload(callback)
        return true
    }

    private fun doUpload(callback: (IHandler, PublishMessage) -> Unit) {
        val articleDraft = draft.getArticleDraft()
        if (articleDraft == null) {
            w("ArticleUploadHandler", "doUpload error because articleDraft = null")
            next(callback)
            return
        }
        val uploadDraft = articleDraft.getUploadDraft()
        if (uploadDraft == null) {
            w("ArticleUploadHandler", "doUpload error because uploadDraft = null")
            next(callback)
            return
        }
        when (uploadDraft.getUploadType()) {
            IUploadAttachmentDraft.UploadAttachmentType.PIC -> {
                val picAttachmentDraft = uploadDraft as PicAttachmentDraft
                if (picAttachmentDraft.picList.isEmpty()) {
                    callback(this, PublishMessage(maxProgress, PublishMessage.PublishState.RUNNING))
                    next(callback)
                } else {
                    val doneSignal = CountDownLatch(picAttachmentDraft.picList.size)
                    val progressArray = Array(picAttachmentDraft.picList.size) { 0F }
                    val uploadHandlers = mutableListOf<PicUploadRunnable>()

                    picAttachmentDraft.picList.forEachIndexed { index, value ->
                        val picUploadHandler = PicUploadRunnable(index, doneSignal, value) { i: Int, progress: Float ->
                            progressArray[i] = progress
                            callback(this, PublishMessage(getTotalProgress(progressArray), PublishMessage.PublishState.RUNNING))
                        }
                        uploadHandlers.add(picUploadHandler)
                        BgDispatcher.dispatch(picUploadHandler)
                    }
                    doneSignal.await()
                    var isSuccess = true
                    uploadHandlers.forEach {
                        val pic = it
                        if (it.isUploadSuccess) {
                            val richItem = articleDraft.getRichContent()?.richItemList?.findLast {
                                val target = if (TextUtils.isEmpty(it.icon)) it.source else it.icon
                                TextUtils.equals(pic.picItemDraft.localFileName, target)
                            }
                            richItem?.icon = it.picItemDraft.url
                            richItem?.source = it.picItemDraft.url
                        }
                        isSuccess = isSuccess && it.isUploadSuccess
                    }
                    if (isSuccess) {
                        next(callback)
                    } else {
                        callback(this, PublishMessage(getTotalProgress(progressArray), PublishMessage.PublishState.ERROR))
                    }
                }
            }
            IUploadAttachmentDraft.UploadAttachmentType.VIDEO -> {
                val videoAttachmentDraft = uploadDraft as VideoAttachmentDraft
                val size = if (videoAttachmentDraft.videoCoverDraft == null
                        || (TextUtils.isEmpty(videoAttachmentDraft.videoCoverDraft?.uploadLoadFileName)
                                && TextUtils.isEmpty(videoAttachmentDraft.videoCoverDraft?.localFileName))) 1 else 2
                val doneSignal = CountDownLatch(size)
                val progressArray = Array(1) { 0F }
                val videoUploadHandler = VideoUploadRunnable(doneSignal, videoAttachmentDraft) { progress: Float ->
                    progressArray[0] = progress
                    callback(this, PublishMessage(getTotalProgress(progressArray), PublishMessage.PublishState.RUNNING))
                }
                BgDispatcher.dispatch(videoUploadHandler)
                doneSignal.await()
                if (videoUploadHandler.isUploadSuccess) {
                    next(callback)
                } else {
                    callback(this, PublishMessage(getTotalProgress(progressArray), PublishMessage.PublishState.ERROR))
                }
            }
        }
    }

    private fun getTotalProgress(progressArray: Array<Float>): Int {
        val step = maxProgress - minProgress
        var total = 0F
        progressArray.forEach {
            total += it
        }
        return (total / progressArray.size / 100F * (step) + minProgress).toInt()
    }


}