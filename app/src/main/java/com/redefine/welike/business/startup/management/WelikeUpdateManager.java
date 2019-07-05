package com.redefine.welike.business.startup.management;

import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.base.request.UpdateAccountRequest;
import com.redefine.welike.business.startup.management.callback.UpdateCallback;
import com.redefine.welike.business.startup.management.constant.RegisteredConstant;
import com.redefine.welike.business.startup.management.request.FollowUsersRequest;

import java.util.List;

/**
 * @author Created by honglin on 2018/6/13.
 */

public class WelikeUpdateManager {


    private BaseRequest request;

    private UpdateCallback mCallback;

    private int updateType;

    public void reqUpdateUserInfo(Account account, int type, UpdateCallback callback) {
        if (request != null) return;

        this.mCallback = callback;
        updateType = type;
        request = new UpdateAccountRequest(account, MyApplication.getAppContext());
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
            if (mCallback != null) {
                mCallback.onUpdateCallback(null, updateType, ErrorCode.networkExceptionToErrCode(e));
            }
        }
    }

    public void reqFollowUsers(List<String> followUids, UpdateCallback callback) {
        if (request != null) return;

        this.mCallback = callback;
        updateType = RegisteredConstant.LOGIN_UPDATE_FOLLOWS;
        request = new FollowUsersRequest(followUids, MyApplication.getAppContext());
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
            if (mCallback != null) {
                mCallback.onUpdateCallback(null, RegisteredConstant.LOGIN_UPDATE_FOLLOWS, ErrorCode.networkExceptionToErrCode(e));
            }
        }
    }

    private void handleError(BaseRequest request, final int errCode) {
        if (this.request == request) {
            this.request = null;
            if (mCallback != null) {
                mCallback.onUpdateCallback(null, updateType, errCode);
            }
        }
    }

    private void handleSuccess(BaseRequest request, JSONObject result) {
        if (this.request == request) {
            this.request = null;

            if (mCallback != null) {
                mCallback.onUpdateCallback(result, updateType, ErrorCode.ERROR_SUCCESS);
            }
        }
    }


}
