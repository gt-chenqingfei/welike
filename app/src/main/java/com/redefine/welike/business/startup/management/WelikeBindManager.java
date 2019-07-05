package com.redefine.welike.business.startup.management;

import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.startup.management.callback.LoginCallback;
import com.redefine.welike.business.startup.management.constant.RegisteredConstant;
import com.redefine.welike.business.startup.management.request.ThirdBindRequest;

/**
 * @author Created by honglin on 2018/6/13.
 */

public class WelikeBindManager {


    private ThirdBindRequest request;

    private LoginCallback mCallback;

    private int loginType;

    public void reqLogin(String mobile, String SMSCode, LoginCallback callback) {

        if (request != null) return;
        loginType = RegisteredConstant.LOGIN_SOURCE_PHONE;
        this.mCallback = callback;

        request = new ThirdBindRequest(MyApplication.getAppContext());
        try {
            request.req("0", mobile, SMSCode, new RequestCallback() {

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
            request = null;
            if (mCallback != null) {
                mCallback.onLoginCallback(null, loginType, ErrorCode.networkExceptionToErrCode(e));
            }
        }
    }


    public void reqFacebookLogin(String facebookToken, LoginCallback callback) {
        if (request != null) return;
        loginType = RegisteredConstant.LOGIN_SOURCE_FACEBOOK;
        this.mCallback = callback;

        request = new ThirdBindRequest(MyApplication.getAppContext());
        try {
            (request).req("1", facebookToken, new RequestCallback() {

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
            request = null;
            if (mCallback != null) {
                mCallback.onLoginCallback(null, loginType, ErrorCode.networkExceptionToErrCode(e));
            }
        }
    }

    public void reqGoogleLogin(String googleToken, LoginCallback callback) {
        if (request != null) {
            return;
        }
        loginType = RegisteredConstant.LOGIN_SOURCE_GOOGLE;
        this.mCallback = callback;
        request = new ThirdBindRequest(MyApplication.getAppContext());
        try {
            ((ThirdBindRequest) request).req("2", googleToken, new RequestCallback() {

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
            request = null;
            if (mCallback != null) {
                mCallback.onLoginCallback(null, loginType, ErrorCode.networkExceptionToErrCode(e));
            }
        }
    }

    public void reqTrueCallerLogin(String payload, String signature, String signatureAlgorithm, LoginCallback callback) {
        if (request != null) {
            return;
        }
        loginType = RegisteredConstant.LOGIN_SOURCE_TRUE_CALLER;
        this.mCallback = callback;
        request = new ThirdBindRequest(MyApplication.getAppContext());
        try {
            ((ThirdBindRequest) request).req("3", payload, signature, signatureAlgorithm, new RequestCallback() {
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
            request = null;
            if (mCallback != null) {
                mCallback.onLoginCallback(null, loginType, ErrorCode.networkExceptionToErrCode(e));
            }
        }
    }


    private void handleError(BaseRequest request, final int errCode) {
        if (this.request == request) {
            this.request = null;
            if (mCallback != null) {
                mCallback.onLoginCallback(null, loginType, errCode);
            }
        }
    }

    private void handleSuccess(BaseRequest request, JSONObject result) {
        if (this.request == request) {
            this.request = null;
            HalfLoginManager.getInstancce().isExistAccount = false;
            if (mCallback != null) {
                mCallback.onLoginCallback(result, loginType, ErrorCode.ERROR_SUCCESS);
            }
        }
    }


}
