package com.redefine.welike.business.user.management.provider;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.browse.management.request.UserLikePostsRequest;
import com.redefine.welike.business.browse.management.request.UserLikePostsRequest2;
import com.redefine.welike.business.common.PostsProviderBase;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.parser.PostsDataSourceParser;

import java.util.List;

/**
 * Created by liubin on 2018/2/26.
 */

public class UserLikePostsProvider extends PostsProviderBase implements RequestCallback {
    private BaseRequest userLikePostsRequest;
    private String cursor;
    private int actionState = GlobalConfig.LIST_ACTION_NONE;
    private UserLikePostsProviderCallback listener;

    public interface UserLikePostsProviderCallback {

        void onRefreshPosts(final List<PostBase> posts, final String uid, final int newCount, final int errCode);

        void onReceiveHisPosts(final List<PostBase> posts, final String uid, final boolean last, final int errCode);

    }

    public void setListener(UserLikePostsProviderCallback listener) {
        this.listener = listener;
    }

    public void tryRefreshPosts(String uid, boolean isBrowse) {
        if (userLikePostsRequest != null) return;

        cursor = null;
        actionState = GlobalConfig.LIST_ACTION_REFRESH;
//        userLikePostsRequest = new UserLikePostsRequest(MyApplication.getAppContext());
        try {
            if (!isBrowse) {
                UserLikePostsRequest request = new UserLikePostsRequest(uid, null, MyApplication.getAppContext());
                request.req(this);
                userLikePostsRequest = request;
            } else {
                UserLikePostsRequest2 request = new UserLikePostsRequest2(uid, null, MyApplication.getAppContext());
                request.req(this);
                userLikePostsRequest = request;
            }
//            userLikePostsRequest.refresh(uid, isBrowse, this);
        } catch (Exception e) {
            e.printStackTrace();
            userLikePostsRequest = null;
            if (listener != null) {
                listener.onRefreshPosts(null, uid, 0, ErrorCode.networkExceptionToErrCode(e));
            }
        }
    }

    public void tryHisPosts(String uid, boolean isBrowse) {
        if (userLikePostsRequest != null) return;

        actionState = GlobalConfig.LIST_ACTION_HIS;
        if (!TextUtils.isEmpty(cursor)) {
//            userLikePostsRequest = new UserLikePostsRequest(MyApplication.getAppContext());
            try {
//                userLikePostsRequest.his(uid, isBrowse, cursor, this);
                if (!isBrowse) {
                    UserLikePostsRequest request = new UserLikePostsRequest(uid, cursor, MyApplication.getAppContext());
                    request.req(this);
                    userLikePostsRequest = request;
                } else {
                    UserLikePostsRequest2 request = new UserLikePostsRequest2(uid, cursor, MyApplication.getAppContext());
                    request.req(this);
                    userLikePostsRequest = request;
                }
            } catch (Exception e) {
                e.printStackTrace();
                userLikePostsRequest = null;
                callReceiveHisPosts(null, uid, ErrorCode.networkExceptionToErrCode(e));
            }
        } else {
            if (listener != null) {
                listener.onReceiveHisPosts(null, uid, true, ErrorCode.ERROR_SUCCESS);
            }
        }
    }

    @Override
    public void onError(BaseRequest request, int errCode) {
        if (request == userLikePostsRequest) {
            String uid = getUidFromRequest(userLikePostsRequest);
            userLikePostsRequest = null;
            if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
                if (listener != null) {
                    listener.onRefreshPosts(null, uid, 0, errCode);
                }
            } else if (actionState == GlobalConfig.LIST_ACTION_HIS) {
                callReceiveHisPosts(null, uid, errCode);
            }
        }
    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (request == userLikePostsRequest) {
            String uid = getUidFromRequest(userLikePostsRequest);
            userLikePostsRequest = null;
            List<PostBase> posts = PostsDataSourceParser.parsePosts(result);
            cursor = PostsDataSourceParser.parseCursor(result);
            List<PostBase> list = filterPosts(posts);
            if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
                int newCount = refreshNewCount(list);
                cacheFirstPage(list);
                if (listener != null) {
                    listener.onRefreshPosts(list, uid, newCount, ErrorCode.ERROR_SUCCESS);
                }
            } else if (actionState == GlobalConfig.LIST_ACTION_HIS) {
                callReceiveHisPosts(list, uid, ErrorCode.ERROR_SUCCESS);
            }
        }
    }

    private void callReceiveHisPosts(List<PostBase> posts, String uid, int errCode) {
        if (listener != null) {
            if (!TextUtils.isEmpty(cursor)) {
                listener.onReceiveHisPosts(posts, uid, false, errCode);
            } else {
                listener.onReceiveHisPosts(posts, uid, true, errCode);
            }
        }
    }

    private String getUidFromRequest(BaseRequest request) {
        String uid = null;
        Object o = request.getUserInfo("uid");
        if (o != null && o instanceof String) {
            uid = (String) o;
        }
        return uid;
    }

}
