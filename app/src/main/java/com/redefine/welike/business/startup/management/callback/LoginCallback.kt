package com.redefine.welike.business.startup.management.callback

import com.alibaba.fastjson.JSONObject

/**
 * Created by honglin on 2018/6/13.
 */
interface LoginCallback {

    fun onLoginCallback(result: JSONObject, loginType: Int, errorCode: Int)

}