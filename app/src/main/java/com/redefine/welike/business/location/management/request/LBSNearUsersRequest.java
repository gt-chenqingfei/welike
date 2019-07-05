package com.redefine.welike.business.location.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;

/**
 * Created by liubin on 2018/3/21.
 */

public class LBSNearUsersRequest extends BaseRequest {
    private static final String LBS_NEAR_USER_KEY_PLACE_ID = "placeId";
    private static final String LBS_NEAR_USER_KEY_PAGE_IDX = "page";
    private static final String LBS_NEAR_USER_KEY_PAGE_NUM = "pageSize";

    public LBSNearUsersRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);
    }

    public void tryUsers(String placeId, int pageIdx, RequestCallback callback) throws Exception {
        setHost("lbs/place/users", true);
        setParam(LBS_NEAR_USER_KEY_PLACE_ID, placeId);
        setParam(LBS_NEAR_USER_KEY_PAGE_IDX, pageIdx);
        setParam(LBS_NEAR_USER_KEY_PAGE_NUM, GlobalConfig.USERS_NUM_ONE_PAGE);
        req(callback);
    }

    public void reviewList(String placeId, RequestCallback callback) throws Exception {
        setHost("lbs/place/users", true);
        setParam(LBS_NEAR_USER_KEY_PLACE_ID, placeId);
        setParam(LBS_NEAR_USER_KEY_PAGE_IDX, 0);
        setParam(LBS_NEAR_USER_KEY_PAGE_NUM, GlobalConfig.LBS_NEAR_REVIEW_USERS_COUNT);
        req(callback);
    }

}
