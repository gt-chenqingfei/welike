package com.redefine.welike.business.feeds.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.request.BaseRequest;

/**
 * Created by liubin on 2018/3/2.
 */

public class DeleteFeedRequest extends BaseRequest {

    public DeleteFeedRequest(String pid, Context context) {
        super(BaseHttpReq.REQUEST_METHOD_DELETE, context);

        Account account = AccountManager.getInstance().getAccount();
        if (account != null) {
            setHost("feed/user/" + account.getUid() + "/delete/post/" + pid, true);
        }
    }

}
