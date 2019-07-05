package com.redefine.welike.net

import com.redefine.welike.base.profile.AccountManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class HttpHeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()
        val account = AccountManager.getInstance().account
        val token = account?.uid
        var request: Request
        if (AccountManager.getInstance().isLoginComplete) {
            request = original.newBuilder()
                    .addHeader("Content-type", "application/json;charset=utf-8")
                    .addHeader("idtoken", token ?: "")
                    .method(original.method(), original.body())
                    .build()
        } else {
            request = original.newBuilder()
                    .addHeader("Content-type", "application/json;charset=utf-8")
                    .method(original.method(), original.body())
                    .build()
        }
        return chain.proceed(request)
    }

}