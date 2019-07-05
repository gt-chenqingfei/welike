package com.redefine.welike.business.contact.request

import android.content.Context
import com.google.android.gms.common.util.Base64Utils
import com.redefine.foundation.http.BaseHttpReq.REQUEST_METHOD_PUT
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.base.request.BaseRequest
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class UploadContactsRequest(context: Context, contacts: HashMap<String,String>) : BaseRequest(REQUEST_METHOD_PUT, context) {
    init {
        val account = AccountManager.getInstance().account
        if (account != null) {
            setHost("relationship/contact/" + account.uid + "/batchContact", true)
            val ja = JSONArray()
            contacts.forEach {
                ja.put(JSONObject().put("contactName", it.key).put("phone", it.value))
            }
            setBodyData(ja.toString())
        }
    }
}

class SafeUploadContactsRequest(context: Context, contacts: HashMap<String,String>) : BaseRequest(REQUEST_METHOD_PUT, context) {
    init {
        val account = AccountManager.getInstance().account
        if (account != null) {
            setHost("relationship/contact/" + account.uid + "/batchContactSafe", true)
        }else{
            setHost("skip/relationship/contact/batchContactSafe", true)
        }

        val ja = JSONArray()
        contacts.forEach {
            ja.put(JSONObject().put("contactName", it.key).put("phone", it.value))
        }
        setBodyData(Base64Utils.encode(ja.toString().toByteArray()))
    }
}