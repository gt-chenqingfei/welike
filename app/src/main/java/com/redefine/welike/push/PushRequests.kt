package com.redefine.welike.push

import android.content.Context
import com.redefine.foundation.http.BaseHttpReq
import com.redefine.welike.base.request.BaseRequest

class TokenAddRequest(token: String,  context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_POST, context) {

    init {
        setHost("push/token/add", true)
        setParam("pushToken", token)
    }
}

class UploadTokenRequest(token: String, gaid: String, language: String, context: Context, channel: String) : BaseRequest(BaseHttpReq.REQUEST_METHOD_POST, context) {
    init {
        setHost("push/skip/token/add/off", false)
        setParam("pushToken", token)
        setParam("gaid", gaid)
        setParam("pushType", 1)
        setParam("la", language)
        setParam("channel", channel)
    }
}