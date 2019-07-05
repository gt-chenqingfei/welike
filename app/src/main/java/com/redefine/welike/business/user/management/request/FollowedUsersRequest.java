package com.redefine.welike.business.user.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;

/**
 * Created by liubin on 2018/1/20.
 */

public class FollowedUsersRequest extends BaseRequest {
    public static final String FOLLOWED_USERS_REQ_UID_KEY = "uid";
    private static final String FOLLOWED_USERS_KEY_COUNT = "count";
    private static final String FOLLOWED_USERS_KEY_CURSOR = "cursor";
    private static final String FOLLOWED_USERS_KEY_INDEX = "index";

    public FollowedUsersRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);
    }

    public void refresh(String uid, RequestCallback callback) throws Exception {
        setHost("feed/user/" + uid + "/followed-users", true);
        setParam(FOLLOWED_USERS_KEY_COUNT, GlobalConfig.USERS_NUM_ONE_PAGE);
        putUserInfo(FOLLOWED_USERS_REQ_UID_KEY, uid);
        req(callback);
    }

    public void his(String uid, String cursor, int index, RequestCallback callback) throws Exception {
        setHost("feed/user/" + uid + "/followed-users", true);
        setParam(FOLLOWED_USERS_KEY_COUNT, GlobalConfig.USERS_NUM_ONE_PAGE);
        setParam(FOLLOWED_USERS_KEY_CURSOR, cursor);
        setParam(FOLLOWED_USERS_KEY_INDEX, index);
        putUserInfo(FOLLOWED_USERS_REQ_UID_KEY, uid);
        req(callback);
    }

}
