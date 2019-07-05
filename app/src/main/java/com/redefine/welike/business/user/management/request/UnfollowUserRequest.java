package com.redefine.welike.business.user.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.request.BaseRequest;

/**
 * Created by liubin on 2018/1/19.
 */

public class UnfollowUserRequest extends BaseRequest {
    public static final String UNFOLLOW_UID_KEY = "uid";

    public UnfollowUserRequest(String unfollowUid, Context context) {
        super(BaseHttpReq.REQUEST_METHOD_DELETE, context);

        Account account = AccountManager.getInstance().getAccount();
        if (account != null) {
            setHost("feed/user/" + account.getUid() + "/unfollow/" + unfollowUid, true);
        }
        putUserInfo(UNFOLLOW_UID_KEY, unfollowUid);
    }

}
