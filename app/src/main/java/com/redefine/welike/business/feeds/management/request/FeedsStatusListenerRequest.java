package com.redefine.welike.business.feeds.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.request.BaseRequest;

import java.util.List;

/**
 * Created by liubin on 2018/1/29.
 */

public class FeedsStatusListenerRequest extends BaseRequest {
    private static final String FEEDS_STATUS_KEY_IDS = "id";

    public FeedsStatusListenerRequest(List<String> pids, Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);

        StringBuffer pidArrStr = new StringBuffer();
        for (int i = 0; i < pids.size(); i++) {
            String pid = pids.get(i);
            pidArrStr.append(pid);
            if (i < (pids.size() - 1)) {
                pidArrStr.append(",");
            }
        }
        setHost("feed/post", true);
        setParam(FEEDS_STATUS_KEY_IDS, pidArrStr);
    }

}
