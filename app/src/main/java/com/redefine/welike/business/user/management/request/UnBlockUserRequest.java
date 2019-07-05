package com.redefine.welike.business.user.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.request.BaseRequest;

/**
 * Created by liubin on 2018/1/19.
 */

public class UnBlockUserRequest extends BaseRequest {
    public static final String UN_BLOCK_UID_KEY = "uid";

    public UnBlockUserRequest(String unBlockUid, Context context) {
        super(BaseHttpReq.REQUEST_METHOD_DELETE, context);

        Account account = AccountManager.getInstance().getAccount();
        if (account != null) {
            setHost("relationship/user/" + account.getUid() + "/unblock/" + unBlockUid, true);
        }
        putUserInfo(UN_BLOCK_UID_KEY, unBlockUid);
    }

}
