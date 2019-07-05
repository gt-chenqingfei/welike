package com.redefine.welike.business.startup.management.request;

import android.content.Context;
import android.text.TextUtils;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;

/**
 * Created by liubin on 2018/2/11.
 */

public class SugUsersRequest extends BaseRequest {
    private static final String SUG_USERS_KEY_COUNT_ONE_PAGE = "count";
    private static final String SUG_USERS_KEY_CURSOR = "cursor";

    public SugUsersRequest(String cursor, Context context,String referrerId) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);

        setHost("user/hot", true);
        setParam(SUG_USERS_KEY_COUNT_ONE_PAGE, GlobalConfig.SUG_USERS_NUM_ONE_PAGE);
        if (!TextUtils.isEmpty(cursor)) {
            setParam(SUG_USERS_KEY_CURSOR, cursor);
        }
        setParam("referrerId", referrerId);
    }

}
