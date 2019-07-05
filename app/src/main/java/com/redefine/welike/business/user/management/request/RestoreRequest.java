package com.redefine.welike.business.user.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;

/**
 * Created by honglin on 2018/5/16.
 */

public class RestoreRequest extends BaseRequest {


    public static final int TYPE_RESTORE = 1;
    public static final int TYPE_NEW_ACCOUNT = 2;


    public RestoreRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_POST, context);
        setHost("user/restore", true);
    }


    public void restoreAccount(int status, RequestCallback callback) throws Exception {

        setUrlExtParam("type", String.valueOf(status));

        req(callback);


    }
}
