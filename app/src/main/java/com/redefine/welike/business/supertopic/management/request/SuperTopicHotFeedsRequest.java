package com.redefine.welike.business.supertopic.management.request;

import android.content.Context;
import android.text.TextUtils;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;

public class SuperTopicHotFeedsRequest extends BaseRequest {

    public SuperTopicHotFeedsRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);
    }

    public void posts(String topicId, String cursor, RequestCallback callback) throws Exception {
        setHost("feed/skip/community/" + topicId + "/posts", false);
        setParam("postOrder", "HOT");
        setParam("count", GlobalConfig.POSTS_NUM_ONE_PAGE);
        if (!TextUtils.isEmpty(cursor)) {
            setParam("cursor", cursor);
        }
        req(callback);
    }

}
