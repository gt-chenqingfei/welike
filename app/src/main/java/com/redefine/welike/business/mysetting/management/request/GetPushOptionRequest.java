package com.redefine.welike.business.mysetting.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;

/**
 * Created by mengnan on 2018/6/6.
 **/
public class GetPushOptionRequest extends BaseRequest {
    public GetPushOptionRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);
            setHost("push/switch", true);

    }

    public void check(RequestCallback callback) throws Exception {

        req(callback);

    }
}
