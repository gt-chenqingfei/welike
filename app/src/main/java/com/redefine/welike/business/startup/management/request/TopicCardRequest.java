package com.redefine.welike.business.startup.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.request.BaseRequest;

/**
 * Created by mengnan on 2018/5/28.
 **/
public class TopicCardRequest extends BaseRequest{
    public TopicCardRequest(int method, Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);
        setHost("conplay/topic/resident/skip/all", false);
    }
}
