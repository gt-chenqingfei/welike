package com.redefine.welike.business.feeds.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.request.BaseRequest;

/**
 * Created by liubin on 2018/3/4.
 */

public class PostDetailRequest extends BaseRequest {

    public PostDetailRequest(String pid, Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);

        setHost("feed/post/" + pid, true);
    }

    public PostDetailRequest(String pid, boolean auth, Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context,auth);

        setHost("feed/post/h5/" + pid, auth);
    }

}
