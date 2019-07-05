package com.redefine.commonui.share.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.request.BaseRequest;

/**
 * Created by nianguowang on 2018/9/8
 */
public class ShareSuccessRequest extends BaseRequest {
    public ShareSuccessRequest(Context context, String postId) {
        super(BaseHttpReq.REQUEST_METHOD_POST, context);
        setHost("short/post/share/skip/increase", true);
        setUrlExtParam("postId", postId);
    }
}
