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

public class ContactOptionRequest extends BaseRequest {

    public ContactOptionRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);
        Account account = AccountManager.getInstance().getAccount();
        if (account != null) {
            setHost("relationship/option/" + account.getUid() + "/getContactOption", true);
        }


    }

    public void check(RequestCallback callback) throws Exception {

        req(callback);

    }
}
