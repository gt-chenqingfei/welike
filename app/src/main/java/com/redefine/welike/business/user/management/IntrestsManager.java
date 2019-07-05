package com.redefine.welike.business.user.management;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.startup.management.SuggesterParser;
import com.redefine.welike.business.startup.management.request.IntrestsRequest;
import com.redefine.welike.business.startup.management.request.ReferrerInfo;
import com.redefine.welike.business.user.management.bean.Interest;
import com.redefine.welike.business.user.management.bean.InterestRequestBean;
import com.redefine.welike.business.user.management.request.IntrestsProfileRequest;
import com.redefine.welike.hive.AppsFlyerManager;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by honlin on 2018/2/11.
 */

public class IntrestsManager implements RequestCallback {
    private IntrestsRequest request;
    private IntrestsProfileRequest profileRequest;
    private IntrestsSuggesterCallback listener;
    private int cursorNum;

    public interface IntrestsSuggesterCallback {

        void onRefreshIntrestSuggestions(ArrayList<Interest> intrests, int errCode, ReferrerInfo info);

    }

    public void setListener(IntrestsSuggesterCallback listener) {
        this.listener = listener;
    }

    public void refresh() {
        if (request != null) return;
        cursorNum = 0;
        request = new IntrestsRequest(cursorNum, MyApplication.getAppContext(), AppsFlyerManager.getInstance().getReferrerId());
        try {
            request.req(this);
        } catch (Exception e) {
            e.printStackTrace();
            request = null;
            if (listener != null) {
                listener.onRefreshIntrestSuggestions(null, ErrorCode.networkExceptionToErrCode(e), null);
            }
        }
    }


    public void refreshProfile() {
        if (profileRequest != null) return;
        cursorNum = 0;
        profileRequest = new IntrestsProfileRequest(cursorNum, MyApplication.getAppContext(), AppsFlyerManager.getInstance().getReferrerId());
        try {
            profileRequest.req(this);
        } catch (Exception e) {
            e.printStackTrace();
            profileRequest = null;
            if (listener != null) {
                listener.onRefreshIntrestSuggestions(null, ErrorCode.networkExceptionToErrCode(e), null);
            }
        }
    }


    @Override
    public void onError(BaseRequest request, int errCode) {
        if (request == this.request || request == this.profileRequest) {
            this.request = null;
            this.profileRequest = null;
            final int error = errCode;
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    if (listener != null) {
                        listener.onRefreshIntrestSuggestions(null, error, null);
                    }
                }

            });
        }
    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (request == this.request || request == this.profileRequest) {
            this.request = null;
            this.profileRequest = null;
            Gson gson = new Gson();
            InterestRequestBean bean = gson.fromJson(result.toJSONString(), InterestRequestBean.class);
            final ReferrerInfo info = SuggesterParser.parserReferrer(result);
            if (bean != null) {
                final ArrayList<Interest> intrests = bean.getList();
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onRefreshIntrestSuggestions(intrests, ErrorCode.ERROR_SUCCESS, info);
                        }
                    }
                });
            } else {
                if (listener != null) {
                    listener.onRefreshIntrestSuggestions(null, ErrorCode.ERROR_SUCCESS, null);
                }
            }

        }
    }

}
