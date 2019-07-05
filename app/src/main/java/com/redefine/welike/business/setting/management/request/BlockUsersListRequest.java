package com.redefine.welike.business.setting.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;

public class BlockUsersListRequest extends BaseRequest {
    public static final String FOLLOWING_USERS_REQ_UID_KEY = "uid";
    private static final String FOLLOWING_USERS_KEY_COUNT = "count";
    private static final String FOLLOWING_USERS_KEY_CURSOR = "cursor";

    public BlockUsersListRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);
    }

    public void refresh(String uid, RequestCallback callback) throws Exception {
        setHost("relationship/user/" + uid + "/block-users", true);
        setParam(FOLLOWING_USERS_KEY_COUNT, GlobalConfig.USERS_NUM_ONE_PAGE);
        putUserInfo(FOLLOWING_USERS_REQ_UID_KEY, uid);
        req(callback);
    }

    public void his(String uid, String cursor, RequestCallback callback) throws Exception {
        setHost("relationship/user/" + uid + "/block-users", true);
        setParam(FOLLOWING_USERS_KEY_COUNT, GlobalConfig.USERS_NUM_ONE_PAGE);
        setParam(FOLLOWING_USERS_KEY_CURSOR, cursor);
        putUserInfo(FOLLOWING_USERS_REQ_UID_KEY, uid);
        req(callback);
    }


}
