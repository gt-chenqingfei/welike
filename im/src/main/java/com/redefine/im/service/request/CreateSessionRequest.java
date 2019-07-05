package com.redefine.im.service.request;

import android.content.Context;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.request.BaseRequest;

/**
 * Created by liubin on 2018/2/5.
 */

public class CreateSessionRequest extends BaseRequest {

    public CreateSessionRequest(String uid, Context context) {
        super(BaseHttpReq.REQUEST_METHOD_POST, context);

        Account account = AccountManager.getInstance().getAccount();
        if (account != null) {
            setHost("im/message/conversation/create", true);

            JSONArray uids = new JSONArray();
            JSONObject uidObj = new JSONObject();
            JSONObject ownerObj = new JSONObject();
            uidObj.put("id", uid);
            ownerObj.put("id", account.getUid());
            uids.add(uidObj);
            uids.add(ownerObj);
            setBodyData(uids.toJSONString());
        }
    }

}
