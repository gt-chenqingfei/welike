package com.redefine.welike.business.publisher.management.handler

import android.text.TextUtils
import com.redefine.oss.adapter.UploadMgr
import com.redefine.oss.upload.IUploadTaskCallback
import com.redefine.oss.upload.UploadManager.UPLOAD_TYPE_IMG
import com.redefine.welike.base.io.WeLikeFileManager
import com.redefine.welike.business.publisher.e
import com.redefine.welike.business.publisher.management.draft.PicItemDraft
import java.util.concurrent.CountDownLatch

/**
 *
 * Name: PicUploadRunnable
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-07-11 12:37
 *
 */

class PicUploadRunnable constructor(private val index: Int, private val doneSignal: CountDownLatch, val picItemDraft: PicItemDraft, private val init: (Int, Float) -> Unit) : Runnable {
    var isUploadSuccess: Boolean = false
    var errorCode: Int = 0

    override fun run() {
        if (!TextUtils.isEmpty(picItemDraft.url)) {
            isUploadSuccess = true
            doneSignal.countDown()
            return
        }

        if (picItemDraft.localFileName.contains("http://") || picItemDraft.localFileName.contains("https://")) {
            picItemDraft.url = picItemDraft.localFileName
            isUploadSuccess = true
            doneSignal.countDown()
            return
        }

        val uploadFileName: String = if (TextUtils.isEmpty(picItemDraft.uploadLoadFileName)) {
            picItemDraft.localFileName
        } else {
            picItemDraft.uploadLoadFileName!!
        }
        val suffix = WeLikeFileManager.parseTmpFileSuffix(uploadFileName)
        val objectKey = UploadMgr.upload(uploadFileName, suffix, UPLOAD_TYPE_IMG, object : IUploadTaskCallback {
            override fun onUploadTaskProgress(objKey: String?, progress: Float) {
                init(index, progress)
            }

            override fun onUploadTaskCompleted(objKey: String?, url: String?) {
                e("PicUploadRunnable", "Picture  onUploadTaskCompleted index:$index,url:$url")
                isUploadSuccess = true
                picItemDraft.url = url
                doneSignal.countDown()
            }

            override fun onUploadTaskFailed(objKey: String?, errCode: Int) {
                e("PicUploadRunnable", "Picture  onUploadTaskFailed index:$index,errorCode:$errCode")
                isUploadSuccess = false
                errorCode = errCode
                picItemDraft.url = ""
                doneSignal.countDown()
            }
        })
        picItemDraft.objectKey = objectKey
    }

}