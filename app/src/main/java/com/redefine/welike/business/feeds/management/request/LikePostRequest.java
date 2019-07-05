package com.redefine.welike.business.feeds.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.request.BaseRequest;

/**
 * Created by liubin on 2018/1/20.
 */

public class LikePostRequest extends BaseRequest {

    public LikePostRequest(String pid, Context context) {
        super(BaseHttpReq.REQUEST_METHOD_PUT, context);

        Account account = AccountManager.getInstance().getAccount();
        if (account != null) {
            setHost("feed/user/" + account.getUid() + "/like/post/" + pid, true);
        }
    }

}
