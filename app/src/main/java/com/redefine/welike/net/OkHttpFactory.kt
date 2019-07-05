package com.redefine.welike.net

import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Name: OkHttpFactory
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-08-23 22:59
 */
class OkHttpFactory private constructor() {

    lateinit var okHttpClient: OkHttpClient

    companion object {
        private const val HTTP_CONNECTION_TIMEOUT = 10
        private const val HTTP_READ_TIMEOUT = 20

        var debug = true
        fun getInstance() = Holder.INSTANCE
    }

    private object Holder {
        val INSTANCE = OkHttpFactory()
    }

    init {
        okHttpClient = if (!debug) {
            OkHttpClient.Builder()
                    .connectTimeout(HTTP_CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .readTimeout(HTTP_READ_TIMEOUT.toLong(), TimeUnit.SECONDS).addInterceptor(HttpHeaderInterceptor())
                    .addInterceptor(HttpRequestInterceptor()).build()
        } else {
            OkHttpClient.Builder()
                    .connectTimeout(HTTP_CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .readTimeout(HTTP_READ_TIMEOUT.toLong(), TimeUnit.SECONDS).addInterceptor(HttpHeaderInterceptor())
                    .addInterceptor(HttpRequestInterceptor()).addNetworkInterceptor(StethoInterceptor()).build()
        }
    }

}
