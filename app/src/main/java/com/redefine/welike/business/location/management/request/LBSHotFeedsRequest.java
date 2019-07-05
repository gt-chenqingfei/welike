package com.redefine.welike.business.location.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;

/**
 * Created by mengnan on 2018/5/31.
 **/
public class LBSHotFeedsRequest extends BaseRequest{
    private static final String LBS_NEAR_USER_KEY_PLACE_ID = "locationId";
    private static final String LBS_NEAR_USER_KEY_PAGE_NUM = "pageSize";

    public LBSHotFeedsRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);
    }

    public void tryFeeds(String placeId,String cursor,  RequestCallback callback) throws Exception {
        setHost("leaderboard/location/24h", true);
        setParam(LBS_NEAR_USER_KEY_PLACE_ID, placeId);
        setParam("order", "created");
        setParam("cursor", cursor);
        setParam(LBS_NEAR_USER_KEY_PAGE_NUM, GlobalConfig.POSTS_NUM_ONE_PAGE);
        req(callback);
    }
}
