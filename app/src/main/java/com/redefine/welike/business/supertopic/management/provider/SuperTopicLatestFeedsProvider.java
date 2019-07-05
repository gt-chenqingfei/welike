package com.redefine.welike.business.supertopic.management.provider;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.common.PostsProviderBase;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.parser.PostsDataSourceParser;
import com.redefine.welike.business.feeds.management.provider.ISinglePostsProvider;
import com.redefine.welike.business.feeds.management.provider.SinglePostsProviderCallback;
import com.redefine.welike.business.supertopic.management.request.SuperTopicFeedsRequest;

import java.util.List;

public class SuperTopicLatestFeedsProvider extends PostsProviderBase implements ISinglePostsProvider, RequestCallback {
    private SuperTopicFeedsRequest feedsRequest;
    private String cursor;
    private int actionState = GlobalConfig.LIST_ACTION_NONE;
    private SinglePostsProviderCallback listener;
    private String topicId;

    public SuperTopicLatestFeedsProvider(String topicId) {
        super();
        this.topicId = topicId;
    }

    @Override
    public void setListener(SinglePostsProviderCallback listener) {
        this.listener = listener;
    }

    @Override
    public void tryRefreshPosts() {
        if (feedsRequest != null) return;
        cursor = null;
        cacheList.clear();
        actionState = GlobalConfig.LIST_ACTION_REFRESH;

        feedsRequest = new SuperTopicFeedsRequest(MyApplication.getAppContext());
        try {
            feedsRequest.posts(topicId, null, this);
        } catch (Exception e) {
            e.printStackTrace();
            feedsRequest = null;
            if (listener != null) {
                listener.onRefreshPosts(null, 0, false, ErrorCode.networkExceptionToErrCode(e));
            }
        }
    }

    @Override
    public void tryHisPosts() {
        if (feedsRequest != null) return;

        if (cursor != null) {
            actionState = GlobalConfig.LIST_ACTION_HIS;
            feedsRequest = new SuperTopicFeedsRequest(MyApplication.getAppContext());
            try {
                feedsRequest.posts(topicId, cursor, this);
            } catch (Exception e) {
                e.printStackTrace();
                feedsRequest = null;
                if (listener != null) {
                    listener.onReceiveHisPosts(null, false, ErrorCode.networkExceptionToErrCode(e));
                }
            }
        } else {
            if (listener != null) {
                listener.onReceiveHisPosts(null, true, ErrorCode.ERROR_SUCCESS);
            }
        }
    }

    @Override
    public void attachListener() {

    }

    @Override
    public void detachListener() {

    }

    @Override
    public void onError(BaseRequest request, int errCode) {
        if (request == feedsRequest) {
            feedsRequest = null;
            if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
                if (listener != null) {
                    listener.onRefreshPosts(null, 0, false, errCode);
                }
            } else if (actionState == GlobalConfig.LIST_ACTION_HIS) {
                if (listener != null) {
                    listener.onReceiveHisPosts(null, false, errCode);
                }
            }
        }
    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (request == feedsRequest) {
            feedsRequest = null;
            List<PostBase> posts = PostsDataSourceParser.parsePosts(result);
            cursor = PostsDataSourceParser.parseCursor(result);
            List<PostBase> list = filterPosts(posts);
            if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
                int newCount = refreshNewCount(list);
                cacheFirstPage(list);
                if (listener != null) {
                    listener.onRefreshPosts(list, newCount, TextUtils.isEmpty(cursor), ErrorCode.ERROR_SUCCESS);
                }
            } else if (actionState == GlobalConfig.LIST_ACTION_HIS) {
                if (!TextUtils.isEmpty(cursor)) {
                    if (listener != null) {
                        listener.onReceiveHisPosts(list, false, ErrorCode.ERROR_SUCCESS);
                    }
                } else {
                    if (listener != null) {
                        listener.onReceiveHisPosts(list, true, ErrorCode.ERROR_SUCCESS);
                    }
                }
            }
        }
    }

}
