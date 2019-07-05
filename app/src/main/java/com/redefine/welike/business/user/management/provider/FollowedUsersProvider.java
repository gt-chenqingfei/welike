package com.redefine.welike.business.user.management.provider;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.common.UsersProviderBase;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.management.parser.UserParser;
import com.redefine.welike.business.user.management.request.FollowedUsersRequest;

import java.util.List;

/**
 * Created by liubin on 2018/1/20.
 */

public class FollowedUsersProvider extends UsersProviderBase implements IUsersProvider, RequestCallback {
    private FollowedUsersRequest usersRequest;
    private String cursor;
    private int index;
    private int actionState = GlobalConfig.LIST_ACTION_NONE;
    private UsersProviderCallback listener;

    public void setListener(UsersProviderCallback listener) {
        this.listener = listener;
    }

    @Override
    public void tryRefreshUsers(String uid) {
        if (usersRequest != null) return;

        actionState = GlobalConfig.LIST_ACTION_REFRESH;
        cursor = null;
        index = 0;
        cacheList.clear();
        usersRequest = new FollowedUsersRequest(MyApplication.getAppContext());
        try {
            usersRequest.refresh(uid, this);
        } catch (Exception e) {
            e.printStackTrace();
            usersRequest = null;
            if (listener != null) {
                listener.onRefreshUsers(null, uid, 0, ErrorCode.networkExceptionToErrCode(e));
            }
        }
    }

    @Override
    public void tryHisUsers(String uid) {
        if (usersRequest != null) return;

        actionState = GlobalConfig.LIST_ACTION_HIS;
        if (!TextUtils.isEmpty(cursor)) {
            usersRequest = new FollowedUsersRequest(MyApplication.getAppContext());
            try {
                usersRequest.his(uid, cursor, index, this);
            } catch (Exception e) {
                e.printStackTrace();
                usersRequest = null;
                callReceiveHisContacts(null, uid, ErrorCode.networkExceptionToErrCode(e));
            }
        } else {
            if (listener != null) {
                listener.onReceiveHisUsers(null, uid, true, ErrorCode.ERROR_SUCCESS);
            }
        }
    }

    @Override
    public void onError(BaseRequest request, int errCode) {
        if (request == usersRequest) {
            String uid = getUidFromRequest(usersRequest);
            usersRequest = null;
            if (actionState == GlobalConfig.LIST_ACTION_HIS) {
                callReceiveHisContacts(null, uid, errCode);
            } else if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
                if (listener != null) {
                    listener.onRefreshUsers(null, uid, 0, errCode);
                }
            }
        }
    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (request == usersRequest) {
            String uid = getUidFromRequest(usersRequest);
            List<User> users = UserParser.parseUsers(result);
            if (users != null && users.size() > 0) {
                index += 1;
            }
            cursor = UserParser.parseCursor(result);
            usersRequest = null;
            List<User> list = filterUsers(users);
            if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
                int newCount = refreshNewCount(list);
                cacheFirstPage(list);
                if (listener != null) {
                    listener.onRefreshUsers(list, uid, newCount, ErrorCode.ERROR_SUCCESS);
                }
            } else if (actionState == GlobalConfig.LIST_ACTION_HIS) {
                callReceiveHisContacts(list, uid, ErrorCode.ERROR_SUCCESS);
            }
        }
    }

    private void callReceiveHisContacts(List<User> users, String uid, int errCode) {
        if (listener != null) {
            if (!TextUtils.isEmpty(cursor)) {
                listener.onReceiveHisUsers(users, uid, false, errCode);
            } else {
                listener.onReceiveHisUsers(users, uid, true, errCode);
            }
        }
    }

    private String getUidFromRequest(FollowedUsersRequest request) {
        String uid = null;
        Object o = request.getUserInfo(FollowedUsersRequest.FOLLOWED_USERS_REQ_UID_KEY);
        if (o != null && o instanceof String) {
            uid = (String)o;
        }
        return uid;
    }

}
