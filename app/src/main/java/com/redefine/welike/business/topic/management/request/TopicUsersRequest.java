package com.redefine.welike.business.topic.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;

/**
 * Created by liubin on 2018/3/21.
 */

public class TopicUsersRequest extends BaseRequest {

    public TopicUsersRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);
    }

    public void tryUsers(String topicId, String cursor, RequestCallback callback) throws Exception {
        setHost("feed/topic/" + topicId + "/users", true);
        setParam("order", "created");
        setParam("cursor", cursor);
        setParam("count", GlobalConfig.MAX_TOPIC_USER_COUNT);
        req(callback);
    }

//    public TopicUsersRequest(Context context, boolean auth) {
//        super(BaseHttpReq.REQUEST_METHOD_GET, context, auth);
//    }
//
//    public void tryUsers(String topicId, String cursor, boolean auth, RequestCallback callback) throws Exception {
//        setHost("feed/topic/h5/" + topicId + "/users", auth);
//        setParam("order", "created");
//        setParam("cursor", cursor);
//        setParam("count", GlobalConfig.MAX_TOPIC_USER_COUNT);
//        req(callback);
//    }
}
