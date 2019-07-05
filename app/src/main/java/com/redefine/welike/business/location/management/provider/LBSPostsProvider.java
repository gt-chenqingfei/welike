package com.redefine.welike.business.location.management.provider;

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
import com.redefine.welike.business.location.management.request.LBSFeedsRequest;

import java.util.List;

/**
 * Created by liubin on 2018/3/21.
 */

public class LBSPostsProvider extends PostsProviderBase implements ISinglePostsProvider, RequestCallback {
    private LBSFeedsRequest feedsRequest;
    private String placeId;
    private int pageIdx;
    private boolean last;
    private int actionState = GlobalConfig.LIST_ACTION_NONE;
    private SinglePostsProviderCallback listener;

    public LBSPostsProvider(String placeId) {
        this.placeId = placeId;
    }

    @Override
    public void setListener(SinglePostsProviderCallback listener) {
        this.listener = listener;
    }

    @Override
    public void attachListener() {

    }

    @Override
    public void detachListener() {

    }

    @Override
    public void tryRefreshPosts() {
        if (feedsRequest != null) return;
        pageIdx = 0;
        last = false;
        cacheList.clear();
        actionState = GlobalConfig.LIST_ACTION_REFRESH;

        feedsRequest = new LBSFeedsRequest(MyApplication.getAppContext());
        try {
            feedsRequest.tryFeeds(placeId, pageIdx, this);
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

        if (!last) {
            actionState = GlobalConfig.LIST_ACTION_HIS;
            feedsRequest = new LBSFeedsRequest(MyApplication.getAppContext());
            try {
                feedsRequest.tryFeeds(placeId, pageIdx, this);
            } catch (Exception e) {
                e.printStackTrace();
                feedsRequest = null;
                if (listener != null) {
                    listener.onReceiveHisPosts(null, false, ErrorCode.networkExceptionToErrCode(e));
                }
            }
        } else {
            feedsRequest = null;
            if (listener != null) {
                listener.onReceiveHisPosts(null, true, ErrorCode.ERROR_SUCCESS);
            }
        }
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
            try {
                last = !result.getBoolean("hasMore");
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<PostBase> list = filterPosts(posts);
            pageIdx++;
            if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
                int newCount = refreshNewCount(list);
                cacheFirstPage(list);
                if (listener != null) {
                    listener.onRefreshPosts(list, newCount, last, ErrorCode.ERROR_SUCCESS);
                }
            } else if (actionState == GlobalConfig.LIST_ACTION_HIS) {
                if (listener != null) {
                    listener.onReceiveHisPosts(list, last, ErrorCode.ERROR_SUCCESS);
                }
            }
        }
    }

}
