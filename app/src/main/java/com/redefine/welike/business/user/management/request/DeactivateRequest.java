package com.redefine.welike.business.user.management.request;

import android.content.Context;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;

import java.util.List;

/**
 * Created by honglin on 2018/5/16.
 */

public class DeactivateRequest extends BaseRequest {

    public DeactivateRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_POST, context);
        setHost("user/status", true);

    }


    public void postDeactivateAccount(String uid, int status, List<Integer> reasonIds, RequestCallback callback) throws Exception {


        JSONObject jo = new JSONObject();

        jo.put("userId", uid);

        jo.put("status", status);

        JSONArray jsonArray = new JSONArray();

        jsonArray.addAll(reasonIds);

        jo.put("disableReasonIds", jsonArray);

        setBodyData(jo.toJSONString());

        req(callback);


    }
}
