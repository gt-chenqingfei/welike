package com.redefine.welike.business.startup.management.callback

import com.alibaba.fastjson.JSONObject

/**
 * Created by honglin on 2018/6/13.
 */
interface UpdateCallback {

    fun onUpdateCallback(result: JSONObject, updateType: Int, errorCode: Int)

}