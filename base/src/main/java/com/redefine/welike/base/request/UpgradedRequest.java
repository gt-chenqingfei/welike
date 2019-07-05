package com.redefine.welike.base.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;

/**
 * Created by liubin on 2018/3/6.
 */

public class UpgradedRequest extends BaseRequest {

    public UpgradedRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_POST, context);

        setHost("auth/version", true);
    }

}
