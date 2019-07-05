package com.redefine.welike.business.mysetting.management.request;

import android.content.Context;

import com.google.gson.Gson;
import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.mysetting.management.MutePushOptions;

/**
 * Created by mengnan on 2018/6/6.
 **/
public class PostPushOptionRequest extends BaseRequest {
    public PostPushOptionRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_POST, context);

    }

    public void postPushOptions(MutePushOptions options, RequestCallback callback) throws Exception {
        setHost("push/switch/add", true);
        Gson gson = new Gson();
        setBodyData(gson.toJson(options));
        req(callback);
    }
}
