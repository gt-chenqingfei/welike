package com.redefine.welike.business.startup.management;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.startup.management.request.ListDefaultHeadsRequest;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liubin on 2018/2/11.
 */

public class HeadsSuggester {
    private ListDefaultHeadsRequest request;

    public interface HeadsSuggesterCallback {

        void onHeadsDone(List<String> heads, int errCode);

    }

    public void reqHeads(byte sex, final HeadsSuggesterCallback callback) {
        if (request != null) return;

        request = new ListDefaultHeadsRequest(sex, MyApplication.getAppContext());
        try {
            request.req(new RequestCallback() {

                @Override
                public void onError(BaseRequest request, int errCode) {
                    handleError(request, errCode, callback);
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                    handleSuccess(request, result, callback);
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onHeadsDone(null, ErrorCode.networkExceptionToErrCode(e));
            }
        }
    }

    private void handleError(BaseRequest request, final int errCode, final HeadsSuggesterCallback callback) {
        if (this.request == request) {
            this.request = null;
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    if (callback != null) {
                        callback.onHeadsDone(null, errCode);
                    }
                }
            });

        }
    }

    private void handleSuccess(BaseRequest request, JSONObject result, final HeadsSuggesterCallback callback) {
        if (this.request == request) {
            this.request = null;
            final List<String> heads = new ArrayList<>();
            if (result != null) {
                JSONArray headsJSON = result.getJSONArray("list");
                if (headsJSON != null && headsJSON.size() > 0) {
                    for (int i = 0; i < headsJSON.size(); i++) {
                        String head = headsJSON.getString(i);
                        heads.add(head);
                    }
                }
            }
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    if (callback != null) {
                        callback.onHeadsDone(heads, ErrorCode.ERROR_SUCCESS);
                    }
                }

            });
        }
    }

}
