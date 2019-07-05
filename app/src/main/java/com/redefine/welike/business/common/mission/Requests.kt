package com.redefine.welike.business.common.mission

import android.content.Context
import com.redefine.foundation.http.BaseHttpReq
import com.redefine.welike.base.request.BaseRequest

class GetMissionRequest(context: Context,uid: String) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {
    init {

            setHost("discovery/taskv2/card", true)
            setParam("userId", uid)
    }
}

class FinishRequest(context: Context, val type: Int, uid: String) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {
    init {
        setHost("discovery/taskv2/finish", true)
        setParam("userId", uid)
        setParam("type", type)
    }
}

class SignRequest(uid: String, context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {
    init {
        setHost("discovery/user/sign", true)
        setParam("userId", uid)
        setParam("time", 0)
    }
}