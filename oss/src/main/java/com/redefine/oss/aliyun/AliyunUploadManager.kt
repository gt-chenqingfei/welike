package com.redefine.oss.aliyun

import android.text.TextUtils
import com.alibaba.sdk.android.oss.ClientConfiguration
import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.OSSClient
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback
import com.alibaba.sdk.android.oss.common.OSSLogToFileUtils
import com.alibaba.sdk.android.oss.common.auth.OSSAuthCredentialsProvider
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask
import com.alibaba.sdk.android.oss.model.ObjectMetadata
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.alibaba.sdk.android.oss.model.PutObjectResult
import com.redefine.foundation.utils.CommonHelper
import com.redefine.oss.adapter.AliyunOssUploadConfig
import com.redefine.oss.adapter.IUploadConfig
import com.redefine.oss.adapter.IUploadManager
import com.redefine.oss.upload.ErrorCode
import com.redefine.oss.upload.IUploadTaskCallback


/**
 *
 * Name: UploadMgr
 * Author: weber
 * Email:
 * Comment: //TODO
 * Date: 2018-12-25 22:38
 *
 */

class AliyunUploadManager : IUploadManager {
    private lateinit var mOssClient: OSSClient
    private lateinit var mUploadConfig: AliyunOssUploadConfig
    private val tasks = mutableMapOf<String, OSSAsyncTask<PutObjectResult>>()

    override fun upload(objFileName: String, extName: String, objectType: String, callback: IUploadTaskCallback?): String {
        val objectKey = buildObjectKey(CommonHelper.generateUUID(), extName, objectType)
        val put = PutObjectRequest(mUploadConfig.bucketName, objectKey, objFileName)
        val metadata = ObjectMetadata()
        // 指定Content-Type
        metadata.contentType = "application/octet-stream"
        metadata.setHeader("x-oss-object-acl", "public-read")
        metadata.userMetadata["mts"] = "true"
        put.metadata = metadata

        put.setProgressCallback { _, currentSize, totalSize ->
            callback?.onUploadTaskProgress(objectKey, currentSize * 1.0F / totalSize)
        }
        val asyncPutObject = mOssClient.asyncPutObject(put, object : OSSCompletedCallback<PutObjectRequest, PutObjectResult> {
            override fun onSuccess(request: PutObjectRequest?, result: PutObjectResult?) {
                callback?.onUploadTaskCompleted(objectKey, mUploadConfig.downloadUrl + objectKey)
                synchronized(tasks) {
                    tasks.remove(objectKey)
                }
            }

            override fun onFailure(request: PutObjectRequest?, clientException: ClientException?, serviceException: ServiceException?) {
                callback?.onUploadTaskFailed(objectKey, ErrorCode.ERROR_UPLOAD_NETWORK_INVALID)
                synchronized(tasks) {
                    tasks.remove(objectKey)
                }
            }
        })
        synchronized(tasks) {
            tasks[objectKey] = asyncPutObject
        }
        return objectKey
    }

    private fun buildObjectKey(subObjectKey: String, extName: String, objectType: String): String {
        return if (!TextUtils.isEmpty(extName)) {
            String.format("%s-%s.%s", objectType, subObjectKey, extName)
        } else {
            String.format("%s-%s", objectType, subObjectKey)
        }
    }

    override fun setEnv(uploadConfig: IUploadConfig) {
        OSSLogToFileUtils.getInstance().setUseSdCard(false)
        val config = uploadConfig as AliyunOssUploadConfig
        val credentialProvider = OSSAuthCredentialsProvider(config.stsUrl)
        val conf = ClientConfiguration()
        conf.socketTimeout = 120 * 1000
        conf.maxConcurrentRequest = 9
        mUploadConfig = config
        mOssClient = OSSClient(config.context, config.endPoint, credentialProvider, conf)
    }

    override fun stopAll() {
        synchronized(tasks) {
            tasks.forEach {
                it.value.cancel()
            }
            tasks.clear()
        }
    }
}