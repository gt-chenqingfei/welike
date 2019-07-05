package com.redefine.welike.business.setting.management.provider;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.setting.management.request.BlockUsersListRequest;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.management.parser.UserParser;
import com.redefine.welike.business.user.management.provider.IUsersProvider;
import com.redefine.welike.business.user.management.provider.UsersProviderCallback;

import java.util.List;

public class BlockUsersProvider implements IUsersProvider, RequestCallback {

    private BlockUsersListRequest usersRequest;
    private String cursor;
    private int actionState = GlobalConfig.LIST_ACTION_NONE;
    private UsersProviderCallback listener;
    private String mUid;

    public void setListener(UsersProviderCallback listener) {
        this.listener = listener;
    }

    @Override
    public void tryRefreshUsers(String uid) {
        if (usersRequest != null) return;
        mUid = uid;
        actionState = GlobalConfig.LIST_ACTION_REFRESH;
        cursor = null;
        usersRequest = new BlockUsersListRequest(MyApplication.getAppContext());
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
        mUid = uid;
        actionState = GlobalConfig.LIST_ACTION_HIS;
        if (!TextUtils.isEmpty(cursor)) {
            usersRequest = new BlockUsersListRequest(MyApplication.getAppContext());
            try {
                usersRequest.his(uid, cursor, this);
            } catch (Exception e) {
                e.printStackTrace();
                usersRequest = null;
                callReceiveHisContacts(null, uid, ErrorCode.networkExceptionToErrCode(e));
            }
        } else {
            usersRequest = null;
            if (listener != null) {
                listener.onReceiveHisUsers(null, uid, true, ErrorCode.ERROR_SUCCESS);
            }
        }
    }

    @Override
    public void onError(BaseRequest request, int errCode) {
        if (request == usersRequest) {
            usersRequest = null;
            if (actionState == GlobalConfig.LIST_ACTION_HIS) {
                callReceiveHisContacts(null, mUid, errCode);
            } else if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
                if (listener != null) {
                    listener.onRefreshUsers(null, mUid, 0, errCode);
                }
            }
        }
    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (request == usersRequest) {
            usersRequest = null;
            List<User> users = UserParser.parseUsers(result);

            cursor = UserParser.parseCursor(result);
            if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
                if (listener != null) {
                    int size = users == null ? 0 : users.size();
                    listener.onRefreshUsers(users, mUid, size, ErrorCode.ERROR_SUCCESS);
                }
            } else if (actionState == GlobalConfig.LIST_ACTION_HIS) {
                callReceiveHisContacts(users, mUid, ErrorCode.ERROR_SUCCESS);
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
}
