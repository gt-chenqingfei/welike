package com.redefine.welike.business.startup.management.request

import android.content.Context
import android.text.TextUtils
import com.alibaba.fastjson.JSONObject
import com.redefine.foundation.http.BaseHttpReq
import com.redefine.foundation.utils.CommonHelper
import com.redefine.im.threadTry
import com.redefine.welike.MyApplication
import com.redefine.welike.base.constant.CommonConstant
import com.redefine.welike.base.request.BaseRequest
import com.redefine.welike.base.request.RequestCallback
import com.redefine.welike.base.util.DesUtil
import com.redefine.welike.statistical.utils.GoogleUtil
import com.ta.utdid2.device.UTDevice

/**
 * Created by honglin on 2018/7/24.
 */

class CheckPhoneIsNewRequest(context: Context, phone: String) : BaseRequest(BaseHttpReq.REQUEST_METHOD_POST, context, false) {
    init {
        setHost("user/skip/isexist", false)
        setBodyData(phone)
    }
}


class SkipEditUserInfoRequest(context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_POST, context, true) {
    init {
        setHost("user/skipbaseinfo", true)
        val bodyJSON = JSONObject()
        bodyJSON.put("finishLevel", 4)
        setBodyData(bodyJSON.toJSONString())
    }
}


class CheckLoginStateRequest(context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_POST, context, false) {

    fun request(callback: RequestCallback) {
        threadTry {
            setHost("user/login/half/check", false)
            val bodyJSON = JSONObject()
            GoogleUtil.forceGAID().let {
                if (it.isNotEmpty())
                    bodyJSON.put("halfUserName", DesUtil.bin2hex(DesUtil.encrypt(it + CommonConstant.params1, CommonConstant.params)))
            }
            bodyJSON.put("utdid", DesUtil.bin2hex(DesUtil.encrypt(UTDevice.getUtdid(MyApplication.getAppContext()) + CommonConstant.params1, CommonConstant.params)))
            setBodyData(bodyJSON.toJSONString())
            req(callback)
        }
    }
}

/**
 *  new login request
 *  **/
class HalfLoginRequest(context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_POST, context, false) {

    val mContext: Context = context

    fun tryRequest(nickName: String, callback: RequestCallback) {
        threadTry {
            setHost("user/login/half", false)
            val bodyJSON = JSONObject()
            GoogleUtil.forceGAID().let {
                if (it.isNotEmpty())
                    bodyJSON.put("halfUserName", DesUtil.bin2hex(DesUtil.encrypt(it + CommonConstant.params1, CommonConstant.params)))

            }
            bodyJSON.put("nickName", nickName)
            bodyJSON.put("utdid", DesUtil.bin2hex(DesUtil.encrypt(UTDevice.getUtdid(mContext) + CommonConstant.params1, CommonConstant.params)))
            setBodyData(bodyJSON.toJSONString())
            req(callback)
        }
    }
}



