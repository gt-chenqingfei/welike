package com.redefine.welike.business.assignment.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.request.BaseRequest;

public class BannerRequest extends BaseRequest {

    public BannerRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);

        setHost("conplay/active/banner", true);
    }

    public BannerRequest(Context context, boolean isAuth) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context, isAuth);

        setHost("conplay/skip/active/banner", isAuth);
    }

}
