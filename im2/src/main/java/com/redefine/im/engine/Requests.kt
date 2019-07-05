package com.redefine.im.engine

import android.content.Context
import android.text.TextUtils
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.redefine.foundation.http.BaseHttpReq
import com.redefine.im.room.SESSION
import com.redefine.welike.base.io.WeLikeFileManager
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.base.profile.UploadListener
import com.redefine.welike.base.request.BaseRequest
import com.redefine.welike.base.uploading.UploadingManager
import java.io.File

class ApplySessionCustomerRequest(context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_POST, context) {

    init {
        val account = AccountManager.getInstance().account
        if (account != null) {
            setHost("im/message/conversation/customer", true)

            val uids = JSONArray()
            val ownerObj = JSONObject()
            ownerObj["id"] = account.uid
            uids.add(ownerObj)
            setBodyData(uids.toJSONString())
        }
    }

    fun parseSession(sessionJSON: JSONObject?): SESSION? {
        if (sessionJSON != null) {
            val sid = sessionJSON.getString("id")
            val sessionType = sessionJSON.getString("type")
            val nick = sessionJSON.getString("sessionNickName")
            val head = sessionJSON.getString("sessionImageUrl")
            val time = sessionJSON.getLongValue("created")
            var enableChat = true
            try {
                enableChat = sessionJSON.getBoolean("enableChat") ?: true
            } catch (e: Exception) {
                e.printStackTrace()
            }
            var visableChat = true
            try {
                visableChat = sessionJSON.getBoolean("visableChat") ?: true
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (TextUtils.equals(sessionType, "SINGLE")) {
                val membersJSON = sessionJSON.getJSONArray("members")
                if (membersJSON != null) {
                    val account = AccountManager.getInstance().account
                    if (account != null) {
                        var singleUid: String? = null
                        val iterator = membersJSON.iterator()
                        while (iterator.hasNext()) {
                            val member = iterator.next() as JSONObject
                            val uid = member.getString("id")
                            if (!TextUtils.equals(uid, account.uid)) {
                                singleUid = uid
                                break
                            }
                        }
                        return SESSION(
                                sid = sid,
                                sessionNice = nick,
                                sessionHead = head,
                                sessionUid = singleUid,
                                sessionType = 1,
                                isGreet = 0,
                                time = time,
                                content = "",
                                enableChat = if (enableChat) 1 else 0,
                                msgType = 1,
                                unreadCount = 0,
                                visableChat = if (visableChat) 1 else 0
                        )
                    }
                }
            }
        }
        return null
    }
}

