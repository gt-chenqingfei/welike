package com.redefine.welike.business.feeds.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.request.BaseRequest;

/**
 * Created by nianguowang on 2018/4/16
 */
public class InterestBannerRequest extends BaseRequest {
    public InterestBannerRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);

        setHost("conplay/tag/banner/", true);
    }

}
