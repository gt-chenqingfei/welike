package com.redefine.welike.business.user.management;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.request.BaseRequest;

/**
 * Created by liubin on 2018/1/25.
 */

public class FollowerRequest extends BaseRequest {
    private static final String NOTIFICATIONS_COUNT_KEY_FOLLOWER = "LIKE-after";

    public FollowerRequest(long time, Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);

        Account account = AccountManager.getInstance().getAccount();
        if (account != null) {
            setHost("im/user/" + account.getUid() + "/notifications/count", true);
            setParam(NOTIFICATIONS_COUNT_KEY_FOLLOWER, time);
        }
    }

}