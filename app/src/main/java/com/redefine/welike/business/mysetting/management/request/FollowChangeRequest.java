package com.redefine.welike.business.mysetting.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;

/**
 * Created by liubin on 2018/1/19.
 */

public class FollowChangeRequest extends BaseRequest {

    public FollowChangeRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_PUT, context);
        Account account = AccountManager.getInstance().getAccount();
        if (account != null) {
            setHost("relationship/option/" + account.getUid() + "/followByContact", true);
        }


    }

    public void change(int status, RequestCallback callback) throws Exception {

        setUrlExtParam("status", String.valueOf(status));

        req(callback);

    }
}
