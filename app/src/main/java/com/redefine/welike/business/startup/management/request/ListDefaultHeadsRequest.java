package com.redefine.welike.business.startup.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;

/**
 * Created by liubin on 2018/2/11.
 */

public class ListDefaultHeadsRequest extends BaseRequest {
    private static final String LIST_DEF_HEAD_KEY_SEX = "sex";
    private static final String LIST_DEF_HEAD_KEY_COUNT_ONE_PAGE = "count";

    public ListDefaultHeadsRequest(byte sex, Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);

        setHost("user/default-avatar-url", true);
        setParam(LIST_DEF_HEAD_KEY_SEX, (int)sex);
        setParam(LIST_DEF_HEAD_KEY_COUNT_ONE_PAGE, GlobalConfig.DEFAULT_HEADS_LIST_COUNT);
    }

}
