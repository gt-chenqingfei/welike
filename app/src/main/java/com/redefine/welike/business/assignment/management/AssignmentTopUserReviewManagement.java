package com.redefine.welike.business.assignment.management;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.assignment.management.bean.TopUserShakeBean;
import com.redefine.welike.business.assignment.management.request.UserTopReviewRequest;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liubin on 2018/3/22.
 */

public class AssignmentTopUserReviewManagement {
    private UserTopReviewRequest reviewRequest;

    public interface AssignmentTopUserReviewManagementCallback {

        void onAssignmentTopUserReviewManagementEnd(TopUserShakeBean bean, int errCode);

    }

    public void load(final AssignmentTopUserReviewManagementCallback callback) {
        if (reviewRequest != null) return;

        reviewRequest = new UserTopReviewRequest(MyApplication.getAppContext());
        try {
            reviewRequest.req(new RequestCallback() {

                @Override
                public void onError(BaseRequest request, final int errCode) {
                    if (reviewRequest == request) {
                        reviewRequest = null;
                        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                            @Override
                            public void run() {
                                if (callback != null) {
                                    callback.onAssignmentTopUserReviewManagementEnd(null, errCode);
                                }
                            }

                        });
                    }
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                    if (reviewRequest == request) {
                        reviewRequest = null;
                        JSONArray usersJSON = null;
                        try {
                            usersJSON = result.getJSONArray("users");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        final TopUserShakeBean bean = new TopUserShakeBean();
                        final List<TopUserShakeBean.TopReviewUser> users = new ArrayList<>();
                        if (usersJSON != null && usersJSON.size() > 0) {
                            for (int i = 0; i < usersJSON.size(); i++) {
                                JSONObject uJSON = usersJSON.getJSONObject(i);
                                try {
                                    String uid = uJSON.getString("userId");
                                    String name = uJSON.getString("nickName");
                                    String head = uJSON.getString("avatarUrl");
                                    TopUserShakeBean.TopReviewUser user = new TopUserShakeBean.TopReviewUser();
                                    user.setUid(uid);
                                    user.setName(name);
                                    user.setHead(head);
                                    users.add(user);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        bean.topicUrl = result.getString("forwardUrl");
                        if (result.containsKey("shakeUrl")) {
                            bean.shakeUrl = result.getString("shakeUrl");
                        }
                        if (result.containsKey("title")) {
                            bean.title = result.getString("title");
                        }
                        bean.mTopUsers = users;
                        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                            @Override
                            public void run() {
                                if (callback != null) {
                                    callback.onAssignmentTopUserReviewManagementEnd(bean, ErrorCode.ERROR_SUCCESS);
                                }
                            }

                        });
                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            reviewRequest = null;
            final int errCode = ErrorCode.networkExceptionToErrCode(e);
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    if (callback != null) {
                        callback.onAssignmentTopUserReviewManagementEnd( null, errCode);
                    }
                }

            });
        }
    }

}
