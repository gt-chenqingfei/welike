package com.redefine.welike.business.contact.request

import android.content.Context
import com.redefine.foundation.http.BaseHttpReq
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.base.request.BaseRequest

class FollowAllRequest(context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_PUT, context) {

    init {
        val account = AccountManager.getInstance().account
        if (account != null) {
            setHost("relationship/contact/" + account.uid + "/followall", true)
        }
    }

}