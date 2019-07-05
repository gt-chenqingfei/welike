package com.redefine.welike.business.user.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;

/**
 * Created by honglin on 2018/5/16.
 */

public class DeactivateReasonRequest extends BaseRequest {

    public DeactivateReasonRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);
        setHost("user/deactivate/reason", true);

    }


    public void getDeactivateReason(RequestCallback callback) throws Exception {

        req(callback);

    }
}
