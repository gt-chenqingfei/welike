package com.redefine.welike.business.publisher.management.handler

import android.text.TextUtils
import com.redefine.welike.business.publisher.e
import com.redefine.welike.business.publisher.management.BgDispatcher
import com.redefine.welike.business.publisher.management.PublishAnalyticsManager
import com.redefine.welike.business.publisher.management.draft.IPublishDraft
import com.redefine.welike.business.publisher.management.draft.IUploadAttachmentDraft
import com.redefine.welike.business.publisher.management.draft.PicAttachmentDraft
import com.redefine.welike.business.publisher.management.draft.VideoAttachmentDraft
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 *
 * Name: UploadHandler
 * Author: liwenbo
 * Email:
 * Comment: //负责发布任务上传的责任链
 * Date: 2018-07-11 00:53
 *
 */

class UploadHandler constructor(draft: IPublishDraft, minProgress: Int = 0, maxProgress: Int = 1, next: IHandler?) : AbstractHandler(draft, minProgress, maxProgress, next) {

    private var uploadStartTime: Long = 0
    override fun start(callback: (IHandler, PublishMessage) -> Unit): Boolean {
        if (!super.start(callback)) {
            return false
        }
        callback(this, PublishMessage(minProgress, PublishMessage.PublishState.RUNNING))

        uploadStartTime = System.currentTimeMillis()
        doUpload(callback)
        return true
    }

    private fun doUpload(callback: (IHandler, PublishMessage) -> Unit) {
        val uploadDraft = draft.getUploadDraft()
        if (uploadDraft == null) {
            next(callback)
            return
        }
        when (uploadDraft.getUploadType()) {
            IUploadAttachmentDraft.UploadAttachmentType.PIC -> {
                doUploadPicture(uploadDraft, callback)
            }
            IUploadAttachmentDraft.UploadAttachmentType.VIDEO -> {
                doUploadVideo(uploadDraft, callback)
            }
        }
    }

    private fun doUploadPicture(uploadDraft: IUploadAttachmentDraft, callback: (IHandler, PublishMessage) -> Unit) {
        val picAttachmentDraft = uploadDraft as PicAttachmentDraft
        if (picAttachmentDraft.picList.isEmpty()) {
            callback(this, PublishMessage(maxProgress, PublishMessage.PublishState.RUNNING))
            next(callback)
        } else {
            val doneSignal = CountDownLatch(picAttachmentDraft.picList.size)
            val progressArray = Array(picAttachmentDraft.picList.size) { 0F }
            val uploadRunnableList = mutableListOf<PicUploadRunnable>()
            var awaitTimeout: Long = ((picAttachmentDraft.picList.size + 1) * 60 * 1000).toLong()
            picAttachmentDraft.picList.forEachIndexed { index, value ->
                val picUploadRunnable = PicUploadRunnable(index, doneSignal, value) { i: Int, progress: Float ->
                    progressArray[i] = progress
                    callback(this, PublishMessage(getTotalProgress(progressArray), PublishMessage.PublishState.RUNNING))
                }
                uploadRunnableList.add(picUploadRunnable)
                BgDispatcher.dispatch(picUploadRunnable)
            }
            e("UploadHandler", "await begin")
            doneSignal.await(awaitTimeout, TimeUnit.MILLISECONDS)
            e("UploadHandler", "await end")
            var isSuccess = true
            var errorCode: Int = 0
            uploadRunnableList.forEach {
                isSuccess = isSuccess && it.isUploadSuccess
                if (it.errorCode != 0) {
                    errorCode = it.errorCode
                }
            }
            if (isSuccess) {
                next(callback)
                val uploadEndTime = System.currentTimeMillis()
                PublishAnalyticsManager.getInstance().obtainEventModel(draft.getDraftId()).pictureUploadTime = uploadEndTime - uploadStartTime
            } else {
                val errorMessage = "code:${errorCode}"
                callback(this, PublishMessage(
                        getTotalProgress(progressArray), PublishMessage.PublishState.ERROR,
                        PublishMessage.ErrorCode.PICTURE_UPLOAD_FAIL, errorMessage))
            }
        }
    }

    private fun doUploadVideo(uploadDraft: IUploadAttachmentDraft, callback: (IHandler, PublishMessage) -> Unit) {
        val videoAttachmentDraft = uploadDraft as VideoAttachmentDraft
        val size = if (videoAttachmentDraft.videoCoverDraft == null
                || (TextUtils.isEmpty(videoAttachmentDraft.videoCoverDraft!!.uploadLoadFileName)
                        && TextUtils.isEmpty(videoAttachmentDraft.videoCoverDraft!!.localFileName))) 1 else 2
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

            val uploadEndTime = System.currentTimeMillis()
            PublishAnalyticsManager.getInstance().obtainEventModel(draft.getDraftId()).videoUploadTime = uploadEndTime - uploadStartTime
        } else {
            callback(this, PublishMessage(getTotalProgress(progressArray),
                    PublishMessage.PublishState.ERROR,
                    PublishMessage.ErrorCode.VIDEO_UPLOAD_FAIL,
                    "code:${videoUploadHandler.errorCode}"
            ))
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