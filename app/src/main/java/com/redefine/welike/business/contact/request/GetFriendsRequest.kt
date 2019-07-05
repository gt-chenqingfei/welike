package com.redefine.welike.business.contact.request

import android.content.Context
import com.redefine.foundation.http.BaseHttpReq
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.base.request.BaseRequest

class GetFriendsRequest(context: Context,val cursor: String = "") : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {

    init {
        val account = AccountManager.getInstance().account
        if (account != null) {
            setHost("relationship/contact/" + account.uid + "/combine-users", true)
            setParam("count", 100)
            setParam("cursor", cursor)
        }
    }

}