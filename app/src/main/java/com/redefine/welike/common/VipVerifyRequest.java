package com.redefine.welike.common;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.request.BaseRequest;

/**
 * Created by daining on 2018/4/19.
 */

public class VipVerifyRequest extends BaseRequest {
    public VipVerifyRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);
        setHost("certify/config/fe/", true);
//        setParam("version", version);
    }
}
