package com.redefine.welike.business.supertopic.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.URLCenter;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;

import java.util.List;

public class SuperTopicBatchRequest extends BaseRequest {
    private Context pContext;


    public SuperTopicBatchRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);
        pContext = context;
    }

    public void batch(List<String> topicIds, RequestCallback callback) throws Exception {
        StringBuilder url = new StringBuilder(URLCenter.getHost() + "conplay/community/skip/ids?");

        for (int i = 0; i < topicIds.size(); i++) {
            String u = "ids=" + topicIds.get(i);
            url.append(u);
            url.append("&");
        }
        url.append("welikeParams=").append(baseParamsBlock(pContext));
        setGetMethodUrl(url.toString());
        req(callback);
    }

}
