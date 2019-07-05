package com.redefine.welike.business.assignment.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.request.BaseRequest;

/**
 * Created by mengnan on 2018/5/9.
 **/
public class HomeBannerRequest extends BaseRequest {

    public HomeBannerRequest(Context context) {

        super(BaseHttpReq.REQUEST_METHOD_GET, context);

        setHost("conplay/home/banner", true);
    }
}
