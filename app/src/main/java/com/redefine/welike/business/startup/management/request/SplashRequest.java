package com.redefine.welike.business.startup.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.request.BaseRequest;

/**
 * Created by honglin on 2018/5/10.
 */

public class SplashRequest extends BaseRequest {
    public SplashRequest(int method, Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);
        setHost("conplay/skip/screen", true);
    }


}
