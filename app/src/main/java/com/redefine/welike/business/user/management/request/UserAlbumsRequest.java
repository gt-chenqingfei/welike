package com.redefine.welike.business.user.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;

/**
 * Created by liubin on 2018/1/26.
 */

public class UserAlbumsRequest extends BaseRequest {
    public static final String USER_ALBUMS_REQ_UID_KEY = "uid";
    private static final String USER_ALBUMS_KEY_COUNT = "count";
    private static final String USER_ALBUMS_KEY_CURSOR = "cursor";

    public UserAlbumsRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);
    }

    public void refresh(String uid, RequestCallback callback) throws Exception {
        setHost("feed/user/" + uid + "/image-attachments", true);
        setParam(USER_ALBUMS_KEY_COUNT, GlobalConfig.USER_ALBUMS_NUM_ONE_PAGE);
        putUserInfo(USER_ALBUMS_REQ_UID_KEY, uid);
        req(callback);
    }

    public void his(String uid, String cursor, RequestCallback callback) throws Exception {
        setHost("feed/user/" + uid + "/image-attachments", true);
        setParam(USER_ALBUMS_KEY_COUNT, GlobalConfig.USER_ALBUMS_NUM_ONE_PAGE);
        setParam(USER_ALBUMS_KEY_CURSOR, cursor);
        putUserInfo(USER_ALBUMS_REQ_UID_KEY, uid);
        req(callback);
    }

}
