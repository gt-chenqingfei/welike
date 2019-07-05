package com.redefine.welike.business.startup.management;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.startup.management.request.SplashRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by honglin on 2018/5/10.
 */

public class SplashSuggester {

    private SplashRequest request;

    public interface SplashSuggesterCallback {

        void onSplashDone(List<String> list, int errorCode);

    }

    private SplashSuggesterCallback mCallback;

    public void reqSplash(SplashSuggesterCallback callback) {
        if (request != null) return;

        this.mCallback = callback;

        request = new SplashRequest(BaseHttpReq.REQUEST_METHOD_GET, MyApplication.getAppContext());
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
        }
    }

    private void handleError(BaseRequest request, final int errCode) {
        if (this.request == request) {
            this.request = null;
            if (mCallback != null) {
                mCallback.onSplashDone(null, errCode);
            }
        }
    }

    private void handleSuccess(BaseRequest request, JSONObject result) {
        if (this.request == request) {
            this.request = null;
            final List<String> heads = new ArrayList<>();
            if (result != null) {
                JSONArray splashsJSON = result.getJSONArray("screens");
                if (splashsJSON != null && splashsJSON.size() > 0) {
                    for (int i = 0; i < splashsJSON.size(); i++) {
                        String head = splashsJSON.getString(i);
                        heads.add(head);
                    }
                }
            }
            if (mCallback != null) {
                mCallback.onSplashDone(heads, ErrorCode.ERROR_SUCCESS);
            }
        }
    }

}
