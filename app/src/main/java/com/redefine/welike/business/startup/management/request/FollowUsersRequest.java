package com.redefine.welike.business.startup.management.request;

import android.content.Context;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.request.BaseRequest;

import java.util.List;

/**
 * Created by liubin on 2018/2/11.
 */

public class FollowUsersRequest extends BaseRequest {

    public FollowUsersRequest(List<String> uids, Context context) {
        super(BaseHttpReq.REQUEST_METHOD_PUT, context);

        Account account = AccountManager.getInstance().getAccount();
        if (account != null) {
            setHost("feed/user/" + account.getUid() + "/batchFollow", true);
            JSONArray uidsJSON = new JSONArray();
            if (uids != null && uids.size() > 0) {
                for (String uid : uids) {
                    JSONObject item = new JSONObject();
                    item.put("id", uid);
                    uidsJSON.add(item);
                }
            }
            setBodyData(uidsJSON.toJSONString());
        }
    }

}
