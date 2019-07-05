package com.redefine.welike.business.location.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;

/**
 * Created by liubin on 2018/3/21.
 */

public class LBSFeedsRequest extends BaseRequest {
    private static final String LBS_NEAR_USER_KEY_PLACE_ID = "placeId";
    private static final String LBS_NEAR_USER_KEY_PAGE_IDX = "page";
    private static final String LBS_NEAR_USER_KEY_PAGE_NUM = "pageSize";

    public LBSFeedsRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);
    }

    public void tryFeeds(String placeId, int pageIdx, RequestCallback callback) throws Exception {
        setHost("lbs/place/feeds", true);
        setParam(LBS_NEAR_USER_KEY_PLACE_ID, placeId);
        setParam(LBS_NEAR_USER_KEY_PAGE_IDX, pageIdx);
        setParam(LBS_NEAR_USER_KEY_PAGE_NUM, GlobalConfig.POSTS_NUM_ONE_PAGE);
        req(callback);
    }

}
