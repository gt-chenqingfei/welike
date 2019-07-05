package com.redefine.welike.business.publisher.management.handler

import android.media.ThumbnailUtils
import android.provider.MediaStore
import com.media.mediasdk.codec.FileUtil
import com.media.mediasdk.codec.MP4HeadInfo
import com.redefine.foundation.utils.CommonHelper
import com.redefine.videoconverter.lib.VideoConverterTool
import com.redefine.videoconverter.lib.utils.ConstUtil
import com.redefine.welike.MyApplication
import com.redefine.welike.base.io.WeLikeFileManager
import com.redefine.welike.business.publisher.d
import com.redefine.welike.business.publisher.e
import com.redefine.welike.business.publisher.management.PublishAnalyticsManager
import com.redefine.welike.business.publisher.management.draft.PicItemDraft
import com.redefine.welike.business.publisher.management.draft.VideoAttachmentDraft
import top.zibin.luban.Luban
import java.io.File
import java.util.concurrent.CountDownLatch

/**
 *
 * Name: IPicCompressHandler
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-07-10 23:01
 *
 */

class VideoCompressRunnable constructor(private val draftId: String, private val doneSignal: CountDownLatch, private val videoAttachmentDraft: VideoAttachmentDraft) : Runnable {

    override fun run() {
        val thumbnail = ThumbnailUtils.createVideoThumbnail(videoAttachmentDraft.videoItemDraft.localFileName, MediaStore.Images.Thumbnails.MINI_KIND)
        val fileName = CommonHelper.generateUUID() + WeLikeFileManager.JPG_TMP_SUFFIX
        var thumbnailFile: File? = null
        if (thumbnail != null) {
            thumbnailFile = WeLikeFileManager.saveToTempCache(thumbnail, fileName)
        }
        if (thumbnailFile != null && thumbnailFile.exists()) {
            val file = Luban.with(MyApplication.getAppContext())
                    .setTargetDir(WeLikeFileManager.getTempCacheDir().absolutePath)
                    .setCompressQuality(80)
                    // 视频缩略图矫正旋转
                    .ignoreBy(0).get(thumbnailFile.absolutePath)

            videoAttachmentDraft.videoCoverDraft = PicItemDraft(file.absolutePath)
            videoAttachmentDraft.videoCoverDraft?.width = file.width
            videoAttachmentDraft.videoCoverDraft?.height = file.height
            videoAttachmentDraft.videoCoverDraft?.uploadLoadFileName = file.absolutePath
        }

        val localFile = File(videoAttachmentDraft.videoItemDraft.localFileName)
        val mConvertTool = VideoConverterTool()
        val inputFilePath = localFile.absolutePath

        mConvertTool.setInputUrl(inputFilePath)

        val isSupport = mConvertTool.isSupportFile(inputFilePath)

        if (isSupport) {
            //设置输出的视频文件

            val isNeedConvert = VideoConverterTool.isNeedConvert(inputFilePath)
            val isVideoFromRecorder = videoAttachmentDraft.videoItemDraft.isVideoFromRecorder
            e("VideoCompressRunnable", "isVideoFromRecorder :$isVideoFromRecorder")
            if (!isNeedConvert || isVideoFromRecorder) {
                onNoNeedConvert(localFile)
                return
            }

            val videoFile = WeLikeFileManager.getTempCacheFile(CommonHelper.generateUUID() + WeLikeFileManager.MP4_TMP_SUFFIX)
            mConvertTool.outputFilePath = videoFile.absolutePath
            val startConvertTime = System.currentTimeMillis()
            mConvertTool.setCallBack(object : VideoConverterTool.CallBack {
                override fun progress() {

                }

                override fun finished() {
                    e("VideoCompressRunnable", "Video Compress finished")
                    onConvertFinish(mConvertTool)
                    val endConvertTime = System.currentTimeMillis()
                    val m = PublishAnalyticsManager.getInstance().obtainEventModel(draftId)
                    m?.let {
                        it.setVideoConvertTime(endConvertTime - startConvertTime)
                    }
                }

                override fun Failed() {
                    onConvertFailed(localFile)
                }

                override fun canceled() {
                    onConvertFailed(localFile)
                }

            })

            val result = mConvertTool.convert()
            if (result != ConstUtil.COUVERTINT) {
                onConvertFailed(localFile)
            }
        } else {
            onNoNeedConvert(localFile)
        }
    }

    private fun onConvertFinish(mConvertTool: VideoConverterTool) {
        d("VideoCompressRunnable", "onConvertFinish")
        val resultFile = mConvertTool.outputFilePath
        val mp4HeadInfo = MP4HeadInfo()
        FileUtil.MP4FileAtomReady(resultFile, mp4HeadInfo)
        if (!mp4HeadInfo.exist_moov || !mp4HeadInfo.exist_mdat) {
            val localFile = File(videoAttachmentDraft.videoItemDraft.localFileName)
            onConvertFailed(localFile)
            return
        }

        if (!mp4HeadInfo.is_moov_before_mdat) {
            val videoFile = WeLikeFileManager.getTempCacheFile(CommonHelper.generateUUID() + WeLikeFileManager.MP4_TMP_SUFFIX)
            val isSuccess = FileUtil.MP4FileAtomMove(resultFile, videoFile.absolutePath)
            if (isSuccess) {
                videoAttachmentDraft.videoItemDraft.uploadLoadFileName = videoFile.absolutePath
            } else {
                val localFile = File(videoAttachmentDraft.videoItemDraft.localFileName)
                onConvertFailed(localFile)
                return
            }
        } else {
            videoAttachmentDraft.videoItemDraft.uploadLoadFileName = mConvertTool.outputFilePath
        }
        doneSignal.countDown()
    }

    private fun onConvertFailed(localFile: File) {
        e("VideoCompressRunnable", "onConvertFailed")
//        val resultFile = WeLikeFileManager.copyToTempCache(localFile, localFile.name + WeLikeFileManager.TMP_SUFFIX)
//        videoAttachmentDraft.videoItemDraft.uploadLoadFileName = resultFile.absolutePath
        doneSignal.countDown()
    }

    private fun onNoNeedConvert(localFile: File) {
        val resultFile = WeLikeFileManager.copyToTempCache(localFile, localFile.name + WeLikeFileManager.TMP_SUFFIX)
        videoAttachmentDraft.videoItemDraft.uploadLoadFileName = resultFile.absolutePath
        doneSignal.countDown()
    }
}