package com.redefine.welike.business.feeds.management.provider;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.browse.management.request.UserPostsRequest;
import com.redefine.welike.business.browse.management.request.UserPostsRequest2;
import com.redefine.welike.business.common.PostsProviderBase;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.parser.PostsDataSourceParser;

import java.util.List;

/**
 * Created by liubin on 2018/1/11.
 */

public class UserPostsProvider extends PostsProviderBase implements RequestCallback {
    private BaseRequest userPostsRequest;
    private String cursor;
    private int actionState = GlobalConfig.LIST_ACTION_NONE;
    private UserPostsProviderCallback listener;

    public interface UserPostsProviderCallback {

        void onRefreshPosts(final List<PostBase> posts, final int newCount, final String uid, final int errCode);

        void onReceiveHisPosts(final List<PostBase> posts, final String uid, final boolean last, final int errCode);

    }

    public void setListener(UserPostsProviderCallback listener) {
        this.listener = listener;
    }

    public void tryRefreshPosts(String uid, boolean isBrowse) {
        if (userPostsRequest != null) return;

        cursor = null;
        actionState = GlobalConfig.LIST_ACTION_REFRESH;
        cacheList.clear();
//        userPostsRequest = new UserPostsRequest(MyApplication.getAppContext());
        try {
            if (isBrowse) {
                UserPostsRequest2 request = new UserPostsRequest2(uid, null, MyApplication.getAppContext());
                request.req(this);
                userPostsRequest = request;
            } else {
                UserPostsRequest request = new UserPostsRequest(uid, null, MyApplication.getAppContext());
                request.req(this);
                userPostsRequest = request;
            }

//            userPostsRequest.refresh(uid, isBrowse,this);
        } catch (Exception e) {
            e.printStackTrace();
            userPostsRequest = null;
            if (listener != null) {
                listener.onRefreshPosts(null, 0, uid, ErrorCode.networkExceptionToErrCode(e));
            }
        }
    }

    public void tryHisPosts(String uid, boolean isBrowse) {
        if (userPostsRequest != null) return;

        actionState = GlobalConfig.LIST_ACTION_HIS;
        if (!TextUtils.isEmpty(cursor)) {
//            userPostsRequest = new UserPostsRequest(MyApplication.getAppContext());
            try {
//                userPostsRequest.his(uid, isBrowse, cursor, this);
                if (isBrowse) {
                    UserPostsRequest2 request = new UserPostsRequest2(uid, cursor, MyApplication.getAppContext());
                    request.req(this);
                    userPostsRequest = request;
                } else {
                    UserPostsRequest request = new UserPostsRequest(uid, cursor, MyApplication.getAppContext());
                    request.req(this);
                    userPostsRequest = request;
                }
            } catch (Exception e) {
                e.printStackTrace();
                userPostsRequest = null;
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
        if (request == userPostsRequest) {
            String uid = getUidFromRequest(userPostsRequest);
            userPostsRequest = null;
            if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
                if (listener != null) {
                    listener.onRefreshPosts(null, 0, uid, errCode);
                }
            } else if (actionState == GlobalConfig.LIST_ACTION_HIS) {
                callReceiveHisPosts(null, uid, errCode);
            }
        }
    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (request == userPostsRequest) {
            String uid = getUidFromRequest(userPostsRequest);
            userPostsRequest = null;
            List<PostBase> posts = PostsDataSourceParser.parsePosts(result);
            cursor = PostsDataSourceParser.parseCursor(result);
            List<PostBase> list = filterPosts(posts);
            if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
                int newCount = refreshNewCount(list);
                cacheFirstPage(list);
                if (listener != null) {
                    listener.onRefreshPosts(list, newCount, uid, ErrorCode.ERROR_SUCCESS);
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
