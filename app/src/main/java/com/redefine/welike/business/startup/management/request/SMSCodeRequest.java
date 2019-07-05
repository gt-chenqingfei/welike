package com.redefine.welike.business.startup.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.BuildConfig;
import com.redefine.welike.base.request.BaseRequest;

/**
 * Created by liubin on 2018/1/9.
 */

public class SMSCodeRequest extends BaseRequest {

    public SMSCodeRequest(String mobile, String nationCode, Context context) {
        super(BaseHttpReq.REQUEST_METHOD_POST, context);

        setHost("user/login/sms", false);
        setParam("phoneNumber", mobile);
        if (!BuildConfig.DEBUG && !nationCode.equalsIgnoreCase("91")) {
            setParam("nationCode", "91");
        } else {
            setParam("nationCode", nationCode);
        }
    }

}
