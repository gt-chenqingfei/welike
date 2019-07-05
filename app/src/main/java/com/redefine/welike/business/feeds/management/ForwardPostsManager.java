package com.redefine.welike.business.feeds.management;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.framework.SingleListenerManagerBase;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.browse.management.request.ForwardedPostsRequest;
import com.redefine.welike.business.browse.management.request.ForwardedPostsRequest2;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.parser.PostsDataSourceParser;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liubin on 2018/1/20.
 */

public class ForwardPostsManager extends SingleListenerManagerBase implements RequestCallback {
    private BaseRequest forwardedPostsRequest;
    private String cursor;
    private int actionState = GlobalConfig.LIST_ACTION_NONE;

    public interface ForwardPostsCallback {

        void onRefreshForwardPosts(ForwardPostsManager manager, List<PostBase> posts, String forwardedFid, int errCode);

        void onReceiveHisForwardPosts(ForwardPostsManager manager, List<PostBase> posts, String forwardedFid, boolean last, int errCode);

    }

    public ForwardPostsManager() {
    }

    public void setListener(ForwardPostsCallback listener) {
        super.setListener(listener);
    }

    public void tryRefresh(final String forwardedPid, boolean isAuth) {
        if (forwardedPostsRequest != null) return;

        cursor = null;
        actionState = GlobalConfig.LIST_ACTION_REFRESH;
        try {
            if (isAuth) {
                ForwardedPostsRequest request = new ForwardedPostsRequest(forwardedPid, null);
                request.req(this);
                forwardedPostsRequest = request;
            } else {
                ForwardedPostsRequest2 request = new ForwardedPostsRequest2(forwardedPid, null);
                request.req(this);
                forwardedPostsRequest = request;
            }

        } catch (Exception e) {
            e.printStackTrace();
            forwardedPostsRequest = null;
            final int errCode = ErrorCode.networkExceptionToErrCode(e);
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    ForwardPostsCallback callback = getCallback();
                    if (callback != null) {
                        callback.onRefreshForwardPosts(ForwardPostsManager.this, null, forwardedPid, errCode);
                    }
                }

            });
        }
    }

    public void tryHis(final String forwardedPid, boolean isAuth) {
        if (forwardedPostsRequest != null) return;

        actionState = GlobalConfig.LIST_ACTION_HIS;
        if (!TextUtils.isEmpty(cursor)) {
//            forwardedPostsRequest = new ForwardedPostsRequest(MyApplication.getAppContext());
            try {
                if (isAuth) {
                    ForwardedPostsRequest request = new ForwardedPostsRequest(forwardedPid, cursor);
                    request.req(this);
                    forwardedPostsRequest = request;
                } else {
                    ForwardedPostsRequest2 request = new ForwardedPostsRequest2(forwardedPid, cursor);
                    request.req(this);
                    forwardedPostsRequest = request;
                }
//                if (isAuth) {
//                    ForwardedPostsRequest request = new ForwardedPostsRequest(MyApplication.getAppContext());
//                    request.his(forwardedPid, cursor, this);
//                    forwardedPostsRequest = request;
//                } else {
//                    ForwardedPostsRequest2 request = new ForwardedPostsRequest2(MyApplication.getAppContext());
//                    request.his(forwardedPid, cursor, this);
//                    forwardedPostsRequest = request;
//                }
            } catch (Exception e) {
                e.printStackTrace();
                forwardedPostsRequest = null;
                callReceiveHisForwardPosts(null, forwardedPid, ErrorCode.networkExceptionToErrCode(e));
            }
        } else {
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    ForwardPostsCallback callback = getCallback();
                    if (callback != null) {
                        callback.onReceiveHisForwardPosts(ForwardPostsManager.this, null, forwardedPid, true, ErrorCode.ERROR_SUCCESS);
                    }
                }

            });
        }
    }

    @Override
    public void onError(BaseRequest request, int errCode) {
        if (request == forwardedPostsRequest) {
            final int error = errCode;
            final String pid = getForwardedPidFromRequest(forwardedPostsRequest);
            forwardedPostsRequest = null;
            if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                    @Override
                    public void run() {
                        ForwardPostsCallback callback = getCallback();
                        if (callback != null) {
                            callback.onRefreshForwardPosts(ForwardPostsManager.this, null, pid, error);
                        }
                    }

                });
            } else if (actionState == GlobalConfig.LIST_ACTION_HIS) {
                callReceiveHisForwardPosts(null, pid, error);
            }
        }
    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (request == forwardedPostsRequest) {
            final String pid = getForwardedPidFromRequest(forwardedPostsRequest);
            forwardedPostsRequest = null;
            final List<PostBase> posts = PostsDataSourceParser.parsePosts(result);
            cursor = PostsDataSourceParser.parseCursor(result);
            if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                    @Override
                    public void run() {
                        ForwardPostsCallback callback = getCallback();
                        if (callback != null) {
                            callback.onRefreshForwardPosts(ForwardPostsManager.this, posts, pid, ErrorCode.ERROR_SUCCESS);
                        }
                    }

                });
            } else if (actionState == GlobalConfig.LIST_ACTION_HIS) {
                callReceiveHisForwardPosts(posts, pid, ErrorCode.ERROR_SUCCESS);
            }
        }
    }

    private void callReceiveHisForwardPosts(final List<PostBase> posts, final String pid, final int errCode) {
        final boolean last = TextUtils.isEmpty(cursor);
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                ForwardPostsCallback callback = getCallback();
                if (callback != null) {
                    callback.onReceiveHisForwardPosts(ForwardPostsManager.this, posts, pid, last, errCode);
                }
            }

        });
    }

    private String getForwardedPidFromRequest(BaseRequest request) {
        String pid = null;
        Object o = request.getUserInfo("fid");
        if (o != null && o instanceof String) {
            pid = (String) o;
        }
        return pid;
    }

    private ForwardPostsCallback getCallback() {
        ForwardPostsCallback callback = null;
        Object l = getListener();
        if (l != null && l instanceof ForwardPostsCallback) {
            callback = (ForwardPostsCallback) l;
        }
        return callback;
    }

}
