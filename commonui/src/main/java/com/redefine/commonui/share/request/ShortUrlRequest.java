package com.redefine.commonui.share.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.request.BaseRequest;

public class ShortUrlRequest extends BaseRequest {
    public ShortUrlRequest(Context context, String url) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);
//        setHost("share/link", true);
        setHost("short/share/link",true);
        setParam("param", url);

    }
}
