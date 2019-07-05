package com.redefine.welike.business.startup.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.request.BaseRequest;

/**
 * Created by liubin on 2018/1/6.
 */

public class LoginRequest extends BaseRequest {
    private static final String LOGIN_KEY_USER_NAME = "userName";
    private static final String LOGIN_KEY_CODE = "valideCode";

    public LoginRequest(String mobile, String smsCode, Context context) {
        super(BaseHttpReq.REQUEST_METHOD_POST, context);

        setHost("user/login", false);
        setParam(LOGIN_KEY_USER_NAME, mobile);
        setParam(LOGIN_KEY_CODE, smsCode);
    }

}
