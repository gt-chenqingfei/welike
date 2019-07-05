package com.redefine.welike.business.assignment.management;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.assignment.management.bean.ActiveReviewLayout;
import com.redefine.welike.business.assignment.management.request.ActiveReviewListRequest;
import com.redefine.welike.business.assignment.ui.constant.AssignmentConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liubin on 2018/3/22.
 */

public class AssignmentReviewListManagement {
    private ActiveReviewListRequest reviewListRequest;

    public interface AssignmentReviewListManagementCallback {

        void onAssignmentReviewListManagementEnd(List<ActiveReviewLayout> bannerList, Map<String, Object> extInfo, int errCode);

    }

    public void load(final AssignmentReviewListManagementCallback callback) {
        if (reviewListRequest != null) return;

        reviewListRequest = new ActiveReviewListRequest(MyApplication.getAppContext());
        try {
            reviewListRequest.req(new RequestCallback() {

                @Override
                public void onError(BaseRequest request, final int errCode) {
                    if (reviewListRequest == request) {
                        reviewListRequest = null;
                        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                            @Override
                            public void run() {
                                if (callback != null) {
                                    callback.onAssignmentReviewListManagementEnd(null, null, errCode);
                                }
                            }

                        });
                    }
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                    if (reviewListRequest == request) {
                        reviewListRequest = null;
                        JSONArray layoutsJSON = null;
                        try {
                            layoutsJSON = result.getJSONArray("layouts");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        final List<ActiveReviewLayout> layouts = new ArrayList<>();
                        final Map<String, Object> extInfo = new HashMap<>();
                        if (result.containsKey(AssignmentConstant.KEY_BEGIN_TASK_FINISH)) {
                            boolean beginerFinish = result.getBoolean(AssignmentConstant.KEY_BEGIN_TASK_FINISH);
                            extInfo.put(AssignmentConstant.KEY_BEGIN_TASK_FINISH, beginerFinish);
                        }
                        if (layoutsJSON != null && layoutsJSON.size() > 0) {
                            for (int i = 0; i < layoutsJSON.size(); i++) {
                                JSONObject activeLayoutJSON = layoutsJSON.getJSONObject(i);
                                try {
                                    String button = activeLayoutJSON.getString("button");
                                    String icon = activeLayoutJSON.getString("icon");
                                    String title = activeLayoutJSON.getString("title");
                                    String url = activeLayoutJSON.getString("url");
                                    int actionStatus = activeLayoutJSON.getIntValue("actionStatus");
                                    ActiveReviewLayout layout = new ActiveReviewLayout();
                                    layout.setButton(button);
                                    layout.setIcon(icon);
                                    layout.setTitle(title);
                                    layout.setUrl(url);
                                    layout.setActionStatus(actionStatus);
                                    layouts.add(layout);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                            @Override
                            public void run() {
                                if (callback != null) {
                                    callback.onAssignmentReviewListManagementEnd(layouts, extInfo, ErrorCode.ERROR_SUCCESS);
                                }
                            }

                        });
                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            reviewListRequest = null;
            final int errCode = ErrorCode.networkExceptionToErrCode(e);
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    if (callback != null) {
                        callback.onAssignmentReviewListManagementEnd(null, null, errCode);
                    }
                }

            });
        }
    }

}
