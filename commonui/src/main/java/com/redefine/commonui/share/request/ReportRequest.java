package com.redefine.commonui.share.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.request.BaseRequest;

import org.json.JSONObject;


public class ReportRequest extends BaseRequest {
    public ReportRequest(Context context, JSONObject jsonObject) {
        super(BaseHttpReq.REQUEST_METHOD_PUT, context);
//        setHost("share/record", true);
        setHost("short/share/record", true);
        setBodyData(jsonObject.toString());
    }
}
