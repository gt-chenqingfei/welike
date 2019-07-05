package com.redefine.welike.business.feeds.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;

/**
 * Created by nianguowang on 2018/4/16
 */
public class InterestCategoryUserRequest extends BaseRequest {

    private static final String REQUEST_PARAM_TAG = "tagId";
    private static final String REQUEST_PARAM_PAGE_NUM = "pageNum";
    private static final String REQUEST_PARAM_PAGE_SIZE = "pageSize";

    public InterestCategoryUserRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);

        setHost("user/tag/users/", true);
    }

    public void refresh(String tag, int pageNum, RequestCallback callback) throws Exception {
        setParam(REQUEST_PARAM_TAG, tag);
        setParam(REQUEST_PARAM_PAGE_NUM, pageNum);
        setParam(REQUEST_PARAM_PAGE_SIZE, GlobalConfig.INTEREST_USER_MORE_PAGE);
        req(callback);
    }

    public void his(String tag, int pageNum, RequestCallback callback) throws Exception {
        setParam(REQUEST_PARAM_TAG, tag);
        setParam(REQUEST_PARAM_PAGE_NUM, pageNum);
        setParam(REQUEST_PARAM_PAGE_SIZE, GlobalConfig.INTEREST_USER_MORE_PAGE);
        req(callback);
    }
}
