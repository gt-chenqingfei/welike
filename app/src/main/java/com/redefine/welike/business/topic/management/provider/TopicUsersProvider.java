package com.redefine.welike.business.topic.management.provider;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.common.UsersProviderBase;
import com.redefine.welike.business.topic.management.bean.TopicUser;
import com.redefine.welike.business.topic.management.request.TopicUsersRequest;

import java.util.List;

/**
 * Created by liubin on 2018/3/21.
 */

public class TopicUsersProvider extends UsersProviderBase implements RequestCallback {
    private TopicUsersRequest usersRequest;
    private String topicId;
    private String cursor;
    private boolean last;
    private int actionState = GlobalConfig.LIST_ACTION_NONE;
    private TopicUserProviderCallback listener;

    public interface TopicUserProviderCallback {

        void onRefreshTopicUsers(final List<TopicUser> users, final int errCode);

        void onReceiveHisTopicUsers(final List<TopicUser> users, final boolean last, final int errCode);

    }

    public TopicUsersProvider(String topicId) {
        this.topicId = topicId;
    }

    public void setListener(TopicUserProviderCallback listener) {
        this.listener = listener;
    }

    public void tryRefreshUsers() {
        if (usersRequest != null) return;

        actionState = GlobalConfig.LIST_ACTION_REFRESH;
        cursor = null;
        last = false;
        usersRequest = new TopicUsersRequest(MyApplication.getAppContext());
        try {
            usersRequest.tryUsers(topicId, cursor, this);
        } catch (Exception e) {
            e.printStackTrace();
            usersRequest = null;
            callReceiveNewContacts(null, ErrorCode.networkExceptionToErrCode(e));
        }
    }

    public void tryHisUsers() {
        if (usersRequest != null) return;

        actionState = GlobalConfig.LIST_ACTION_HIS;
        if (!last) {
            usersRequest = new TopicUsersRequest(MyApplication.getAppContext());
            try {
                usersRequest.tryUsers(topicId, cursor, this);
            } catch (Exception e) {
                e.printStackTrace();
                usersRequest = null;
                callReceiveHisContacts(null, ErrorCode.networkExceptionToErrCode(e));
            }
        } else {
            usersRequest = null;
            callReceiveHisContacts(null, ErrorCode.ERROR_SUCCESS);
        }
    }

    @Override
    public void onError(BaseRequest request, int errCode) {
        if (request == usersRequest) {
            usersRequest = null;
            if (actionState == GlobalConfig.LIST_ACTION_HIS) {
                callReceiveHisContacts(null, errCode);
            } else if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
                callReceiveNewContacts(null, errCode);
            }
        }
    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (request == usersRequest) {
            List<TopicUser> users = TopicUser.parse(result);
            try {
                last = !result.getBoolean("hasMore");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                cursor = result.getString("cursor");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (TextUtils.isEmpty(cursor)) {
                last = true;
            }
            usersRequest = null;
            if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
                callReceiveNewContacts(users, ErrorCode.ERROR_SUCCESS);
            } else if (actionState == GlobalConfig.LIST_ACTION_HIS) {
                callReceiveHisContacts(users, ErrorCode.ERROR_SUCCESS);
            }
        }
    }

    private void callReceiveNewContacts(List<TopicUser> users, int errCode) {
        if (listener != null) {
            listener.onRefreshTopicUsers(users, errCode);
        }
    }

    private void callReceiveHisContacts(List<TopicUser> users, int errCode) {
        if (listener != null) {
            listener.onReceiveHisTopicUsers(users, last, errCode);
        }
    }

}
