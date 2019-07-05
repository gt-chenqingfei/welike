package com.redefine.welike.business.assignment.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.request.BaseRequest;

/**
 * Created by liubin on 2018/3/22.
 */

public class ActiveReviewListRequest extends BaseRequest {

    public ActiveReviewListRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);

        setHost("discovery/active/reviewList", true);
    }

}
