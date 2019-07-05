package com.redefine.welike.business.startup.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;

public class ThirdLoginRequest extends BaseRequest {

    public ThirdLoginRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_POST, context);

        setHost("user/login/third", false);
    }

    public void req(String type, String thirdToken, RequestCallback callback) throws Exception {
        setUrlExtParam("loginType", type);
        setParam("token", thirdToken);
        req(callback);
    }

    public void req(String type, String payload, String signature, String signatureAlgorithm, RequestCallback callback) throws Exception {
        setUrlExtParam("loginType", type);
        setParam("payload", payload);
        setParam("signature", signature);
        setParam("signatureAlgorithm", signatureAlgorithm);
        req(callback);
    }

}
