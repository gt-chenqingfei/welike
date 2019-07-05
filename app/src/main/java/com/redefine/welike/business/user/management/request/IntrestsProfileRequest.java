package com.redefine.welike.business.user.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;

/**
 * Created by liubin on 2018/2/8.
 */

public class IntrestsProfileRequest extends BaseRequest {
    private static final String INTRESTS_KEY_PAGE_NUM = "pageNumber";
    private static final String INTRESTS_KEY_COUNT_ONE_PAGE = "pageSize";

    public IntrestsProfileRequest(int pageNum, Context context, String referrerId) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);

        setHost("user/interest/profile/list", true);
        setParam(INTRESTS_KEY_PAGE_NUM, pageNum);
        setParam(INTRESTS_KEY_COUNT_ONE_PAGE, GlobalConfig.INTRESTS_NUM_ONE_PAGE);
        setParam("referrerId", referrerId);
    }

}
