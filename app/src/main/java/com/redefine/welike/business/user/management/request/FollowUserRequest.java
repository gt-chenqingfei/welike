package com.redefine.welike.business.user.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.request.BaseRequest;

/**
 * Created by liubin on 2018/1/19.
 */

public class FollowUserRequest extends BaseRequest {
    public static final String FOLLOW_UID_KEY = "uid";

    public FollowUserRequest(String followUid, Context context) {
        super(BaseHttpReq.REQUEST_METHOD_PUT, context);

        Account account = AccountManager.getInstance().getAccount();
        if (account != null) {
            setHost("feed/user/" + account.getUid() + "/follow/" + followUid, true);
        }
        putUserInfo(FOLLOW_UID_KEY, followUid);
    }

}
