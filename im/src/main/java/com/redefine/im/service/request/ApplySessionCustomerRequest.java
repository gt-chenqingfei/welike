package com.redefine.im.service.request;

import android.content.Context;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.request.BaseRequest;

/**
 * Created by liubin on 2018/3/2.
 */

public class ApplySessionCustomerRequest extends BaseRequest {

    public ApplySessionCustomerRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_POST, context);

        Account account = AccountManager.getInstance().getAccount();
        if (account != null) {
            setHost("im/message/conversation/customer", true);

            JSONArray uids = new JSONArray();
            JSONObject ownerObj = new JSONObject();
            ownerObj.put("id", account.getUid());
            uids.add(ownerObj);
            setBodyData(uids.toJSONString());
        }
    }

}
