package com.redefine.welike.net

import android.content.Context
import android.text.TextUtils
import android.util.Base64
import com.redefine.foundation.utils.ChannelHelper
import com.redefine.foundation.utils.CommonHelper
import com.redefine.welike.MyApplication
import com.redefine.welike.base.LanguageSupportManager
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.statistical.utils.GoogleUtil
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.net.URLEncoder


/**
 *
 * Name: HttpRequestInterceptor
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-07-13 17:05
 *
 */
class HttpRequestInterceptor  : Interceptor {

    override fun intercept(chain: Interceptor.Chain?): Response {
        var request = chain!!.request()
        request = handlerRequest(request)
        return chain.proceed(request)
    }

    private fun handlerRequest(request: Request?): Request? {
        val account = AccountManager.getInstance().account
        val builder = request!!.url().newBuilder()


        request.url().queryParameterNames()?.forEachIndexed { index, s ->
            builder.setEncodedQueryParameter(s, urlEncode(request.url().queryParameterValue(index)))
        }

        if (account != null) {
            builder.addEncodedQueryParameter("token", urlEncode(account.accessToken))
        }
        val params = baseParamsBlock(MyApplication.getAppContext())

        if (!TextUtils.isEmpty(params)) {
            builder.addEncodedQueryParameter("welikeParams", urlEncode(params!!))
        }
        return request.newBuilder().url(builder.build()).build()
    }

    private fun urlEncode(str: String): String {
        return try {
            URLEncoder.encode(str, "UTF-8")

        } catch (e: Throwable) {
            str
        }
    }

    private fun baseParamsBlock(context: Context): String? {
        val version = CommonHelper.getAppVersionName(context)
        val language = LanguageSupportManager.getInstance().currentMenuLanguageType
        val os = "android"
        val deviceId = CommonHelper.getDeviceId(context)
        val source = CommonHelper.getDeviceModel()
        val channel = CommonHelper.getChannel(context)
        val appType = CommonHelper.getAppType(context)
        val gaid = GoogleUtil.gaid
        val tag = ChannelHelper.getTagString(context)

        var commonParam = "ve=$version"
        if (!TextUtils.isEmpty(language)) {
            commonParam = "$commonParam&la=$language"
        }
        commonParam = "$commonParam&os=$os"
        if (!TextUtils.isEmpty(deviceId)) {
            commonParam = "$commonParam&de=$deviceId"
        }
        if (!TextUtils.isEmpty(source)) {
            commonParam = "$commonParam&sr=$source"
        }
        if (!TextUtils.isEmpty(channel)) {
            commonParam = "$commonParam&channel=$channel"
        }
        if (!TextUtils.isEmpty(gaid)) {
            commonParam = "$commonParam&gaid=$gaid"
        }
        if (!TextUtils.isEmpty(tag)) {
            commonParam = "$commonParam&tag=$tag"
        }
        commonParam = "$commonParam&appType=$appType"
        var params: String? = null

        try {
            val orgParamsBytes = commonParam.toByteArray(Charsets.UTF_8)
            params = String(Base64.encode(orgParamsBytes, Base64.NO_WRAP), Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return params
    }

}