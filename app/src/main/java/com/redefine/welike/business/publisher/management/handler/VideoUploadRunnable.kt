package com.redefine.welike.business.publisher.management.handler

import android.text.TextUtils
import com.redefine.oss.adapter.UploadMgr
import com.redefine.oss.upload.IUploadTaskCallback
import com.redefine.oss.upload.UploadManager
import com.redefine.welike.base.io.WeLikeFileManager
import com.redefine.welike.business.publisher.e
import com.redefine.welike.business.publisher.management.draft.VideoAttachmentDraft
import com.redefine.welike.business.publisher.w
import java.util.concurrent.CountDownLatch

/**
 *
 * Name: VideoUploadRunnable
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-07-11 12:37
 *
 */

class VideoUploadRunnable constructor(private val doneSignal: CountDownLatch, private val videoAttachmentDraft: VideoAttachmentDraft, val callback: (Float) -> Unit) : Runnable {
    var isUploadSuccess: Boolean = false
    var errorCode = 0

    override fun run() {
        if (!TextUtils.isEmpty(videoAttachmentDraft.videoItemDraft.url)) {
            isUploadSuccess = true
            doneSignal.countDown()
        } else {
            val uploadFileName: String = if (TextUtils.isEmpty(videoAttachmentDraft.videoItemDraft.uploadLoadFileName)) {
                videoAttachmentDraft.videoItemDraft.localFileName
            } else {
                videoAttachmentDraft.videoItemDraft.uploadLoadFileName!!
            }
            val suffix = WeLikeFileManager.parseTmpFileSuffix(uploadFileName)
            val objectKey = UploadMgr.upload(uploadFileName, suffix, UploadManager.UPLOAD_TYPE_VIDEO, object : IUploadTaskCallback {
                override fun onUploadTaskProgress(objKey: String?, progress: Float) {
                    callback(progress)
                }

                override fun onUploadTaskCompleted(objKey: String?, url: String?) {
                    w("Publish Video Upload", "Video  onUploadTaskCompleted")
                    isUploadSuccess = true
                    videoAttachmentDraft.videoItemDraft.url = url
                    doneSignal.countDown()
                }

                override fun onUploadTaskFailed(objKey: String?, errCode: Int) {
                    e("Publish Video Upload", "Video onUploadTaskFailed errorCode:$errCode")
                    errorCode = errCode
                    isUploadSuccess = false
                    videoAttachmentDraft.videoItemDraft.url = ""
                    doneSignal.countDown()
                }
            })
            videoAttachmentDraft.videoItemDraft.objectKey = objectKey
        }

        var uploadVideoCoverFileName: String? = null

        if (!TextUtils.isEmpty(videoAttachmentDraft.videoCoverDraft?.url)) {
            isUploadSuccess = true
            doneSignal.countDown()
        }
        if (videoAttachmentDraft.videoCoverDraft != null) {
            uploadVideoCoverFileName = if (TextUtils.isEmpty(videoAttachmentDraft.videoCoverDraft?.uploadLoadFileName)) {
                videoAttachmentDraft.videoCoverDraft?.localFileName
            } else {
                videoAttachmentDraft.videoCoverDraft?.uploadLoadFileName
            }
        }
        if (!TextUtils.isEmpty(uploadVideoCoverFileName)) {
            val coverSuffix = WeLikeFileManager.parseTmpFileSuffix(uploadVideoCoverFileName!!)
            val coverObjectKey = UploadMgr.upload(uploadVideoCoverFileName, coverSuffix, UploadManager.UPLOAD_TYPE_IMG, object : IUploadTaskCallback {
                override fun onUploadTaskProgress(objKey: String?, progress: Float) {
                }

                override fun onUploadTaskCompleted(objKey: String?, url: String?) {
                    w("Publish Upload", "Video Cover onUploadTaskCompleted")
                    videoAttachmentDraft.videoCoverDraft?.url = url
                    doneSignal.countDown()
                }

                override fun onUploadTaskFailed(objKey: String?, errCode: Int) {
                    e("Publish Upload", "Video Cover onUploadTaskFailed")
                    videoAttachmentDraft.videoCoverDraft?.url = ""
                    doneSignal.countDown()
                }
            })
            videoAttachmentDraft.videoCoverDraft?.objectKey = coverObjectKey
        }
    }

}