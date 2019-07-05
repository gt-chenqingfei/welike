package com.redefine.welike.business.startup.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.request.BaseRequest;

/**
 * Created by liubin on 2018/1/18.
 */

public class AuthRequest extends BaseRequest {
    private static final String AUTH_KEY_REFRESH_TOKEN = "refresh_token";

    public AuthRequest(String refreshToken, Context context) {
        super(BaseHttpReq.REQUEST_METHOD_POST, context);

        setHost("auth/token/refresh", true);
        setParam(AUTH_KEY_REFRESH_TOKEN, refreshToken);
    }

}
