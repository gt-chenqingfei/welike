package com.redefine.welike.business.topic.management.provider;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.browse.management.request.TopicHotFeedRequest;
import com.redefine.welike.business.browse.management.request.TopicHotFeedRequest2;
import com.redefine.welike.business.common.PostsProviderBase;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.parser.PostsDataSourceParser;
import com.redefine.welike.business.feeds.management.provider.ISinglePostsProvider;
import com.redefine.welike.business.feeds.management.provider.SinglePostsProviderCallback;

import java.util.List;

/**
 * Created by mengnan on 2018/5/16.
 **/
public class TopicHotPostsProvider extends PostsProviderBase implements ISinglePostsProvider, RequestCallback {

    private BaseRequest feedsRequest;
    private String cursor;
    private String tid;
    private int actionState = GlobalConfig.LIST_ACTION_NONE;
    private SinglePostsProviderCallback listener;
    private boolean isBrowse;

    public TopicHotPostsProvider(String id, boolean isBrowse) {
        tid = id;
        this.isBrowse = isBrowse;
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

    @Override
    public void tryRefreshPosts() {
        if (feedsRequest != null) return;

        actionState = GlobalConfig.LIST_ACTION_REFRESH;
        cursor = null;
        cacheList.clear();
//        feedsRequest = new TopicHotFeedRequest(MyApplication.getAppContext());
        try {
            if (!isBrowse){
                feedsRequest = new TopicHotFeedRequest(tid,null,MyApplication.getAppContext());
            }else{
                feedsRequest = new TopicHotFeedRequest2(tid,null,MyApplication.getAppContext());
            }
            feedsRequest.req(this);
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

        actionState = GlobalConfig.LIST_ACTION_HIS;
        if (!TextUtils.isEmpty(cursor)) {
//            feedsRequest = new TopicHotFeedRequest(MyApplication.getAppContext());
            try {
                if (!isBrowse){
                    feedsRequest = new TopicHotFeedRequest(tid,cursor,MyApplication.getAppContext());
                }else{
                    feedsRequest = new TopicHotFeedRequest2(tid,cursor,MyApplication.getAppContext());
                }
                feedsRequest.req(this);
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
    public void setListener(SinglePostsProviderCallback callback) {
        this.listener = callback;
    }
}
