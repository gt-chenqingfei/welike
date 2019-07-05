package com.redefine.welike.business.location.management.request;

import android.content.Context;
import android.text.TextUtils;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.request.BaseRequest;

/**
 * Created by liubin on 2018/3/26.
 */

public class LBSPOISearchRequest extends BaseRequest {

    public LBSPOISearchRequest(double lon, double lat, String pageToken, String keyword, Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);

        setHost("lbs/search", true);
        setParam("lng", Double.toString(lon));
        setParam("lat", Double.toString(lat));
        if (!TextUtils.isEmpty(keyword)) {
            setParam("keyword", keyword);
        }
        if (!TextUtils.isEmpty(pageToken)) {
            setParam("pageToken", pageToken);
        }
    }

}
