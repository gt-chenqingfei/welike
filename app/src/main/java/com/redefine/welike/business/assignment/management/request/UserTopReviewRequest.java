package com.redefine.welike.business.assignment.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.request.BaseRequest;

/**
 * Created by liubin on 2018/3/22.
 */

public class UserTopReviewRequest extends BaseRequest {
    private static final int USER_TOP_REVIEW_NUM = 6;

    public UserTopReviewRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);

        setHost("discovery/user/top", true);
        setParam("page", 0);
        setParam("pageSize", USER_TOP_REVIEW_NUM);
    }

}
