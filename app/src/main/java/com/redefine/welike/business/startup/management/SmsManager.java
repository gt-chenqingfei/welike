package com.redefine.welike.business.startup.management;

import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.startup.management.request.SMSCodeRequest;

/**
 * Created by honglin on 2018/6/13.
 */

public class SmsManager {

    private SMSCodeRequest smsCodeRequest;

    public interface SmsManagerCallback {

        void onSmsCallback(int errorCode);

    }

    private SmsManagerCallback mCallback;

    public void reqSmsCode(String mobile, String nationCode, SmsManagerCallback callback) {
        if (smsCodeRequest != null) return;

        this.mCallback = callback;

        smsCodeRequest = new SMSCodeRequest(mobile, nationCode, MyApplication.getAppContext());
        try {
            smsCodeRequest.req(new RequestCallback() {

                @Override
                public void onError(BaseRequest request, int errCode) {
                    handleError(request, errCode);
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                    handleSuccess(request);
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            if (mCallback != null) {
                mCallback.onSmsCallback(ErrorCode.networkExceptionToErrCode(e));
            }
        }
    }

    private void handleError(BaseRequest request, final int errCode) {
        if (this.smsCodeRequest == request) {
            this.smsCodeRequest = null;
            if (mCallback != null) {
                mCallback.onSmsCallback(errCode);
            }
        }
    }

    private void handleSuccess(BaseRequest request) {
        if (this.smsCodeRequest == request) {
            this.smsCodeRequest = null;

            if (mCallback != null) {
                mCallback.onSmsCallback(ErrorCode.ERROR_SUCCESS);
            }
        }
    }


}
