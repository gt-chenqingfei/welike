package com.redefine.welike.business.location.management.request;

import android.content.Context;
import android.text.TextUtils;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.business.location.management.bean.Location;

/**
 * Created by liubin on 2018/3/21.
 */

public class LBSNearInfoRequest extends BaseRequest {
    private static final String LBS_NEAR_INFO_KEY_LNG = "lng";
    private static final String LBS_NEAR_INFO_KEY_LAT = "lat";
    private static final String LBS_NEAR_INFO_PAGE_TOKEN = "pageToken";

    public LBSNearInfoRequest(String pageToken, Context context, final Location location) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);

        setHost("lbs/near", true);
        if (location != null) {
            setParam(LBS_NEAR_INFO_KEY_LNG, location.getLongitude());
            setParam(LBS_NEAR_INFO_KEY_LAT, location.getLatitude());
        }
        if (!TextUtils.isEmpty(pageToken)) {
            setParam(LBS_NEAR_INFO_PAGE_TOKEN, pageToken);
        }
    }

    public LBSNearInfoRequest(String pageToken, Context context, final Location location,boolean isAuth) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context,isAuth);


        if(isAuth){
            setHost("lbs/near",true);
        }else{
            setHost("lbs/h5/skip/near",false);
        }

        if (location != null) {
            setParam(LBS_NEAR_INFO_KEY_LNG, location.getLongitude());
            setParam(LBS_NEAR_INFO_KEY_LAT, location.getLatitude());
        }
        if (!TextUtils.isEmpty(pageToken)) {
            setParam(LBS_NEAR_INFO_PAGE_TOKEN, pageToken);
        }
    }


}
