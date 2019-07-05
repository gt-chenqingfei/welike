package com.redefine.welike.business.feeds.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.request.BaseRequest;

public class TopUserPostRequest extends BaseRequest{
    public TopUserPostRequest(String pid, Context context) {
        super(BaseHttpReq.REQUEST_METHOD_PUT, context);

        Account account = AccountManager.getInstance().getAccount();
        if (account != null) {
            setHost("feed/user/top-post/" + pid, true);
        }
    }
}
