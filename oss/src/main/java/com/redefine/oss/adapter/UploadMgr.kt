package com.redefine.oss.adapter

import com.redefine.oss.aliyun.AliyunUploadManager
import com.redefine.oss.upload.IUploadTaskCallback
import com.redefine.oss.upload.UploadManager

/**
 *
 * Name: UploadMgr
 * Author: weber
 * Email:
 * Comment: //TODO
 * Date: 2018-12-22 01:25
 *
 */

object UploadMgr {
    private var uploadManager: IUploadManager? = null


    fun setEnv(uploadConfig: AliyunOssUploadConfig) {
        uploadManager = AliyunUploadManager()
        uploadManager?.setEnv(uploadConfig)
    }

    fun setEnv(uploadConfig: GatewayUploadConfig) {
        uploadManager = UploadManager()
        uploadManager?.setEnv(uploadConfig)
    }

    fun stopAll() {
        if (uploadManager != null) {
            uploadManager?.stopAll()
        }
    }

    fun upload(objFileName: String, extName: String, objectType: String, callback: IUploadTaskCallback?): String {
        return uploadManager?.upload(objFileName, extName, objectType, callback) ?: ""
    }


}