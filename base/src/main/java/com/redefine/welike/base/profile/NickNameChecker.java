package com.redefine.welike.base.profile;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.NickNameCheckRequest;
import com.redefine.welike.base.request.NickNameCheckRequest2;
import com.redefine.welike.base.request.RequestCallback;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liubin on 2018/2/8.
 */

public class NickNameChecker {
    private static final int MIN_NICK_NAME_LEN = 6;
    private static final int MAX_NICK_NAME_LEN = 30;
    private NickNameCheckRequest request;
    private NickNameCheckRequest2 request2;
    private boolean checking = false;

    public interface NickNameCheckerCallback {

        void onCheckResult(String nickName, int errCode);

    }

    public void check(String nickName, Context context, final NickNameCheckerCallback callback) {
        checking = true;
        if (request != null) {
            request.cancel();
            request = null;
        }
        if (TextUtils.isEmpty(nickName)) {
            checking = false;
            if (callback != null) {
                callback.onCheckResult(nickName, ErrorCode.ERROR_USERINFO_NICKNAME_EMPTY);
            }
        } else {
            if (nickName.length() < MIN_NICK_NAME_LEN) {
                checking = false;
                if (callback != null) {
                    callback.onCheckResult(nickName, ErrorCode.ERROR_USERINFO_NICKNAME_TOO_SHORT);
                }
            } else if (nickName.length() > MAX_NICK_NAME_LEN) {
                checking = false;
                if (callback != null) {
                    callback.onCheckResult(nickName, ErrorCode.ERROR_USERINFO_NICKNAME_TOO_LONG);
                }
            } else {
                request = new NickNameCheckRequest(nickName, context);
                try {
                    request.req(new RequestCallback() {

                        @Override
                        public void onError(BaseRequest request, int errCode) {
                            if (NickNameChecker.this.request == request) {
                                Object o = request.getUserInfo("nickName");
                                NickNameChecker.this.request = null;
                                if (o != null) {
                                    final String nickName = (String) o;
                                    handleError(nickName, errCode, callback);
                                } else {
                                    handleError(null, errCode, callback);
                                }
                            }
                        }

                        @Override
                        public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                            if (NickNameChecker.this.request == request) {
                                Object o = request.getUserInfo("nickName");
                                NickNameChecker.this.request = null;
                                if (o != null) {
                                    final String nickName = (String) o;
                                    handleSuccess(nickName, callback);
                                } else {
                                    handleError(null, ErrorCode.ERROR_USERINFO_NICKNAME_EMPTY, callback);
                                }
                            }
                        }

                    });
                    request.putUserInfo("nickName", nickName);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onCheckResult(nickName, ErrorCode.networkExceptionToErrCode(e));
                    }
                }
            }
        }
    }

    public void check2(String nickName, Context context, final NickNameCheckerCallback callback) {
        checking = true;
        if (request != null) {
            request.cancel();
            request = null;
        }
        if (TextUtils.isEmpty(nickName)) {
            checking = false;
            if (callback != null) {
                callback.onCheckResult(nickName, ErrorCode.ERROR_USERINFO_NICKNAME_EMPTY);
            }
        } else {
            if (nickName.length() < MIN_NICK_NAME_LEN) {
                checking = false;
                if (callback != null) {
                    callback.onCheckResult(nickName, ErrorCode.ERROR_USERINFO_NICKNAME_TOO_SHORT);
                }
            } else if (nickName.length() > MAX_NICK_NAME_LEN) {
                checking = false;
                if (callback != null) {
                    callback.onCheckResult(nickName, ErrorCode.ERROR_USERINFO_NICKNAME_TOO_LONG);
                }
            } else {
                request2 = new NickNameCheckRequest2(nickName, context);
                try {
                    request2.req(new RequestCallback() {

                        @Override
                        public void onError(BaseRequest request, int errCode) {
                            if (NickNameChecker.this.request2 == request) {
                                NickNameChecker.this.request2 = null;
                                handleError(null, errCode, callback);
                            }
                        }

                        @Override
                        public void onSuccess(BaseRequest request, JSONObject result) {

                            Log.e("hl", "onSuccess: " + result.toString());

                            if (NickNameChecker.this.request2 == request) {
                                NickNameChecker.this.request2 = null;
                                handleSuccess(result.toString(), callback);

                            }
                        }

                    });
                    request2.putUserInfo("nickName", nickName);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onCheckResult(nickName, ErrorCode.networkExceptionToErrCode(e));
                    }
                }
            }
        }
    }


    public boolean isChecking() {
        return checking;
    }

    private void handleError(final String nickName, final int errCode, final NickNameCheckerCallback callback) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                checking = false;
                if (callback != null) {
                    callback.onCheckResult(nickName, errCode);
                }
            }

        });
    }

    private void handleSuccess(final String nickName, final NickNameCheckerCallback callback) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                checking = false;
                if (callback != null) {
                    callback.onCheckResult(nickName, ErrorCode.ERROR_SUCCESS);
                }
            }

        });
    }
}
