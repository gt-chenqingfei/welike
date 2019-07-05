package com.redefine.welike.business.user.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;

/**
 * Created by honglin on 2018/5/15.
 */

public class CheckUserRequest extends BaseRequest {

    public static final int TYPE_MOBILE = 0;
    public static final int TYPE_FACEBOOK = 1;
    public static final int TYPE_GOOGLE = 2;
    public static final int TYPE_TRUE_CALLER = 3;

    private static final String PHONE = "phone";
    private static final String fbTOKEN = "fbToken";
    private static final String GOOGLE_TOKEN = "googleToken";

    public CheckUserRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_POST, context);
        setHost("user/account/check", true);
    }

    public void check(String checkInfo, int type, RequestCallback callback) throws Exception {

        String key = PHONE;
        if(type == TYPE_MOBILE) {
            key = PHONE;
        } else if (type == TYPE_FACEBOOK) {
            key = fbTOKEN;
        } else if (type == TYPE_GOOGLE) {
            key = GOOGLE_TOKEN;
        }
        setUrlExtParam(key, checkInfo);

        req(callback);

    }

    public void check(String payload, String signature, String signatureAlgorithm, RequestCallback callback) throws Exception {
        setParam("payload", payload);
        setParam("signature", signature);
        setParam("signatureAlgorithm", signatureAlgorithm);

        req(callback);
    }

}
