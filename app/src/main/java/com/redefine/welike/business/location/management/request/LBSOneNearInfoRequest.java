package com.redefine.welike.business.location.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.request.BaseRequest;

/**
 * Created by liubin on 2018/3/26.
 */

public class LBSOneNearInfoRequest extends BaseRequest {

    public LBSOneNearInfoRequest(String placeId, Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);

        setHost("lbs/nearest", true);
        setParam("placeId", placeId);
    }

}
