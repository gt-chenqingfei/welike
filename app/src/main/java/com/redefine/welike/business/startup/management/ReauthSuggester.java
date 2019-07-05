package com.redefine.welike.business.startup.management;

import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.startup.management.request.AuthRequest;

/**
 * Created by honglin on 2018/5/10.
 */

public class ReauthSuggester {

    private AuthRequest request;

    public interface ReAuthSuggesterCallback {

        void onReAuthDone(JSONObject result, int errorCode);

    }

    private ReAuthSuggesterCallback mCallback;

    public void reqReAuth(String refreshToken, ReAuthSuggesterCallback callback) {
        if (request != null) return;

        this.mCallback = callback;

        request = new AuthRequest(refreshToken, MyApplication.getAppContext());
        try {
            request.req(new RequestCallback() {

                @Override
                public void onError(BaseRequest request, int errCode) {
                    handleError(request, errCode);
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                    handleSuccess(request, result);
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            mCallback.onReAuthDone(null, ErrorCode.networkExceptionToErrCode(e));

        }
    }

    private void handleError(BaseRequest request, final int errCode) {
        if (this.request == request) {
            this.request = null;
            if (mCallback != null) {
                mCallback.onReAuthDone(null, errCode);
            }
        }
    }

    private void handleSuccess(BaseRequest request, JSONObject result) {
        if (this.request == request) {
            this.request = null;
            if (mCallback != null) {
                mCallback.onReAuthDone(result, ErrorCode.ERROR_SUCCESS);
            }
        }
    }

}
