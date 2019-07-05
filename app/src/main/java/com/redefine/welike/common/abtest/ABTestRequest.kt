package com.redefine.welike.common.abtest

import com.redefine.foundation.http.BaseHttpReq
import com.redefine.welike.MyApplication
import com.redefine.welike.base.request.BaseRequest

class ABTestRequest(keys: List<String>) : BaseRequest(BaseHttpReq.REQUEST_METHOD_POST, MyApplication.getAppContext()) {

    init {
        setHost("conplay/skip/abtest", true)
        setParam("functionNames", keys)
    }
}
//class ABTestRequest(keys: List<String>) : BaseRequest(BaseHttpReq.REQUEST_METHOD_POST, MyApplication.getAppContext()) {
//
//    init {
//        setHost("conplay/test/strategy/config/skip/function", true)
//        setParam("functionNames", keys)
//    }
//}