package com.redefine.welike.business.message.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.request.BaseRequest;

/**
 * Created by liubin on 2018/1/25.
 */

public class NotificationsCountRequest extends BaseRequest {

    public NotificationsCountRequest(Context context, String uid) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);
        setHost("im/user/" + uid + "/notifications/count", true);
    }

}
