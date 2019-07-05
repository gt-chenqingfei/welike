package com.redefine.welike.business.feeds.management;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.redefine.foundation.framework.BroadcastManagerBase;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.browse.management.request.RecommendSlideRequest;
import com.redefine.welike.business.browse.management.request.RecommendUserRequest;
import com.redefine.welike.business.browse.management.request.RecommendUserRequest2;
import com.redefine.welike.business.user.management.bean.RecommendSlideUser;
import com.redefine.welike.business.user.management.bean.RecommendSlideUserList;
import com.redefine.welike.business.user.management.bean.RecommendUser;
import com.redefine.welike.business.user.management.bean.RecommendUser1;
import com.redefine.welike.business.user.management.bean.RecommendUserList;
import com.redefine.welike.business.user.management.bean.RecommendUserList1;
import com.redefine.welike.common.abtest.ABKeys;
import com.redefine.welike.common.abtest.ABTest;

import java.util.ArrayList;
import java.util.List;

public class RecommendUserManager extends BroadcastManagerBase {

    private String cursor = null;
    private int pageNum = 1;

    private RecommendSlideRequest recommendUserRequest;

    public void tryRefresh(Context context, final OnRecommendUserListener listener) {

        if (recommendUserRequest != null) return;

        try {
            recommendUserRequest = new RecommendSlideRequest(context);
            cursor = null;
            pageNum = 1;
            recommendUserRequest.request(pageNum, cursor, new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {
                    recommendUserRequest = null;
                    if (listener != null) {
                        listener.onFail(errCode);
                    }
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) {
                    if (request != recommendUserRequest) return;
                    recommendUserRequest = null;
                    Gson gson = new Gson();
                    RecommendSlideUserList recommendUserList = gson.fromJson(result.toString(), RecommendSlideUserList.class);
                    cursor = recommendUserList.getCursor();
                    if (listener != null)
                        listener.onSuccess(recommendUserList);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onFail(ErrorCode.networkExceptionToErrCode(e));
            }
        }

    }

    public void tryHis(Context context, final OnRecommendUserListener listener) {
        if (recommendUserRequest != null) return;

        try {
            recommendUserRequest = new RecommendSlideRequest(context);
            pageNum++;
            recommendUserRequest.request(pageNum, cursor, new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {
                    recommendUserRequest = null;
                    pageNum--;
                    if (listener != null) {
                        listener.onFail(errCode);
                    }
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) {
                    if (request != recommendUserRequest) return;
                    recommendUserRequest = null;
                    Gson gson = new Gson();
                    RecommendSlideUserList recommendUserList = gson.fromJson(result.toString(), RecommendSlideUserList.class);
                    cursor = recommendUserList.getCursor();
                    if (listener != null)
                        listener.onSuccess(recommendUserList);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onFail(ErrorCode.networkExceptionToErrCode(e));
            }
        }
    }

    public interface OnRecommendUserListener {

        void onSuccess(RecommendSlideUserList ls);

        void onFail(int errCode);

    }


}
