package com.redefine.welike.business.startup.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;

/**
 * Created by liubin on 2018/2/8.
 */

public class VerticalRequest extends BaseRequest {
    private static final String INTRESTS_KEY_PAGE_NUM = "pageNumber";
    private static final String INTRESTS_KEY_COUNT_ONE_PAGE = "pageSize";

    public VerticalRequest(int pageNum, Context context, String referrerId) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);

        setHost("user/interest/vertical/list", true);
        setParam(INTRESTS_KEY_PAGE_NUM, pageNum);
        setParam(INTRESTS_KEY_COUNT_ONE_PAGE, GlobalConfig.INTRESTS_NUM_ONE_PAGE);
        setParam("referrerId", referrerId);
    }

    public VerticalRequest(int pageNum, Context context, boolean auth, String referrerId) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context,auth);

        setHost("user/interest/skip/vertical/list", auth);
        setParam(INTRESTS_KEY_PAGE_NUM, pageNum);
        setParam(INTRESTS_KEY_COUNT_ONE_PAGE, GlobalConfig.INTRESTS_NUM_ONE_PAGE);
        setParam("referrerId", referrerId);
    }

}
