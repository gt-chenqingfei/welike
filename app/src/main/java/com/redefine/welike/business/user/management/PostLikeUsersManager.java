package com.redefine.welike.business.user.management;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.framework.SingleListenerManagerBase;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.browse.management.request.PostLikeUsersRequest;
import com.redefine.welike.business.browse.management.request.PostLikeUsersRequest2;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.management.parser.UserParser;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liubin on 2018/1/20.
 */

public class PostLikeUsersManager extends SingleListenerManagerBase implements RequestCallback {
    private BaseRequest usersRequest;
    private String cursor;
    private int actionState = GlobalConfig.LIST_ACTION_NONE;

    public interface PostLikeUsersCallback {

        void onRefreshPostLikeUsers(PostLikeUsersManager manager, List<User> users, String fid, int errCode);

        void onReceiveHisPostLikeUsers(PostLikeUsersManager manager, List<User> users, String fid, boolean last, int errCode);

    }

    public PostLikeUsersManager() {
    }

    public void setListener(PostLikeUsersCallback listener) {
        super.setListener(listener);
    }

    public void tryRefresh(final String fid, final boolean isAuth) {
        if (usersRequest != null) return;

        actionState = GlobalConfig.LIST_ACTION_REFRESH;
        cursor = null;
//        usersRequest = new PostLikeUsersRequest(MyApplication.getAppContext());
        try {
            if (isAuth) {
                PostLikeUsersRequest request = new PostLikeUsersRequest(fid, null, MyApplication.getAppContext());
                request.req(this);
                usersRequest = request;
            } else {
                PostLikeUsersRequest2 request = new PostLikeUsersRequest2(fid, null, MyApplication.getAppContext());
                request.req(this);
                usersRequest = request;
            }
//            if (isAuth)
//                usersRequest.refresh(fid, this);
//            else usersRequest.refresh(fid, isAuth, this);
        } catch (Exception e) {
            e.printStackTrace();
            usersRequest = null;
            final int errCode = ErrorCode.networkExceptionToErrCode(e);
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    PostLikeUsersCallback callback = getCallback();
                    if (callback != null) {
                        callback.onRefreshPostLikeUsers(PostLikeUsersManager.this, null, fid, errCode);
                    }
                }

            });
        }
    }

    public void tryHis(final String fid, final boolean isAuth) {
        if (usersRequest != null) return;

        actionState = GlobalConfig.LIST_ACTION_HIS;
        if (!TextUtils.isEmpty(cursor)) {
//            usersRequest = new PostLikeUsersRequest(MyApplication.getAppContext());
            try {
//                if (isBrowse)
//                    usersRequest.his(fid, cursor, this);
//                else usersRequest.his(fid, cursor, isBrowse, this);
                if (isAuth) {
                    PostLikeUsersRequest request = new PostLikeUsersRequest(fid, cursor, MyApplication.getAppContext());
                    request.req(this);
                    usersRequest = request;
                } else {
                    PostLikeUsersRequest2 request = new PostLikeUsersRequest2(fid, cursor, MyApplication.getAppContext());
                    request.req(this);
                    usersRequest = request;
                }
            } catch (Exception e) {
                e.printStackTrace();
                usersRequest = null;
                callReceiveHisPostLikeUsers(null, fid, ErrorCode.networkExceptionToErrCode(e));
            }
        } else {
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    PostLikeUsersCallback callback = getCallback();
                    if (callback != null) {
                        callback.onReceiveHisPostLikeUsers(PostLikeUsersManager.this, null, fid, true, ErrorCode.ERROR_SUCCESS);
                    }
                }

            });
        }
    }

    @Override
    public void onError(BaseRequest request, int errCode) {
        if (request == usersRequest) {
            final int error = errCode;
            final String fid = getLikeUsersFidFromRequest(usersRequest);
            usersRequest = null;
            if (actionState == GlobalConfig.LIST_ACTION_HIS) {
                callReceiveHisPostLikeUsers(null, fid, error);
            } else if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                    @Override
                    public void run() {
                        PostLikeUsersCallback callback = getCallback();
                        if (callback != null) {
                            callback.onRefreshPostLikeUsers(PostLikeUsersManager.this, null, fid, error);
                        }
                    }

                });
            }
        }
    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (request == usersRequest) {
            final String fid = getLikeUsersFidFromRequest(usersRequest);
            usersRequest = null;
            final List<User> users = UserParser.parseUsers(result);
            cursor = UserParser.parseCursor(result);
            if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                    @Override
                    public void run() {
                        PostLikeUsersCallback callback = getCallback();
                        if (callback != null) {
                            callback.onRefreshPostLikeUsers(PostLikeUsersManager.this, users, fid, ErrorCode.ERROR_SUCCESS);
                        }
                    }

                });
            } else if (actionState == GlobalConfig.LIST_ACTION_HIS) {
                callReceiveHisPostLikeUsers(users, fid, ErrorCode.ERROR_SUCCESS);
            }
        }
    }

    private void callReceiveHisPostLikeUsers(final List<User> users, final String fid, final int errCode) {
        final boolean last = TextUtils.isEmpty(cursor);
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                PostLikeUsersCallback callback = getCallback();
                if (callback != null) {
                    callback.onReceiveHisPostLikeUsers(PostLikeUsersManager.this, users, fid, last, errCode);
                }
            }

        });
    }

    private String getLikeUsersFidFromRequest(BaseRequest request) {
        String fid = null;
        Object o = request.getUserInfo("fid");
        if (o != null && o instanceof String) {
            fid = (String) o;
        }
        return fid;
    }

    private PostLikeUsersCallback getCallback() {
        PostLikeUsersCallback callback = null;
        Object l = getListener();
        if (l != null && l instanceof PostLikeUsersCallback) {
            callback = (PostLikeUsersCallback) l;
        }
        return callback;
    }

}
