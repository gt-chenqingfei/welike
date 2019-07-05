package com.redefine.welike.business.user.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;

/**
 * Created by liubin on 2018/1/20.
 */

public class FollowingUsersRequest extends BaseRequest {
    public static final String FOLLOWING_USERS_REQ_UID_KEY = "uid";
    private static final String FOLLOWING_USERS_KEY_COUNT = "count";
    private static final String FOLLOWING_USERS_KEY_CURSOR = "cursor";
    private static final String FOLLOWING_USERS_KEY_INDEX = "index";

    public FollowingUsersRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);
    }

    public void refresh(String uid, RequestCallback callback) throws Exception {
        setHost("feed/user/" + uid + "/follow-users", true);
        setParam(FOLLOWING_USERS_KEY_COUNT, GlobalConfig.USERS_NUM_ONE_PAGE);
        putUserInfo(FOLLOWING_USERS_REQ_UID_KEY, uid);
        req(callback);
    }

    public void his(String uid, String cursor, int index, RequestCallback callback) throws Exception {
        setHost("feed/user/" + uid + "/follow-users", true);
        setParam(FOLLOWING_USERS_KEY_COUNT, GlobalConfig.USERS_NUM_ONE_PAGE);
        setParam(FOLLOWING_USERS_KEY_CURSOR, cursor);
        setParam(FOLLOWING_USERS_KEY_INDEX, index);
        putUserInfo(FOLLOWING_USERS_REQ_UID_KEY, uid);
        req(callback);
    }

}
