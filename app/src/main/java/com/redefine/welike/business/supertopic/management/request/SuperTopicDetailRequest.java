package com.redefine.welike.business.supertopic.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;

public class SuperTopicDetailRequest extends BaseRequest {

    public SuperTopicDetailRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);
    }

    public void detail(String topicId, RequestCallback callback) throws Exception {
        setHost("conplay/community/skip/" + topicId, false);
        req(callback);
    }

}
