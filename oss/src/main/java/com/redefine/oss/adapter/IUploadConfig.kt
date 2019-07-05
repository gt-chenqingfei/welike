package com.redefine.oss.adapter

import android.content.Context
import okhttp3.OkHttpClient

/**
 *
 * Name: IUploadConfig
 * Author: weber
 * Email:
 * Comment: //TODO
 * Date: 2018-12-25 23:45
 *
 */

interface IUploadConfig {

}

class GatewayUploadConfig(val host: String, val accessToken: String, val okHttpClient: OkHttpClient) : IUploadConfig

class AliyunOssUploadConfig(val context: Context, val stsUrl: String, val endPoint: String, val bucketName: String, val downloadUrl: String) : IUploadConfig