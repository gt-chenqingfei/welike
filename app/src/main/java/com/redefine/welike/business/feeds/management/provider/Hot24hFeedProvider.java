package com.redefine.welike.business.feeds.management.provider;

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
import com.redefine.welike.business.feeds.management.request.Hot24hFeedRequest;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by nianguowang on 2018/4/18
 */
public class Hot24hFeedProvider extends PostsProviderBase implements ISinglePostsProvider, RequestCallback {

    private String cursor;
    private int actionState = GlobalConfig.LIST_ACTION_NONE;
    private SinglePostsProviderCallback listener;
    private Hot24hFeedRequest request;

    @Override
    public void tryRefreshPosts() {
        if(request != null) {
            return;
        }
        actionState = GlobalConfig.LIST_ACTION_REFRESH;
        // 产品要求：下拉刷新不要清cursor，也带着cursor调接口，其他逻辑不变（这样可能是获取的下一页的数据，但是也添加在列表的前面）
//        cursor = null;
        cacheList.clear();

        request = new Hot24hFeedRequest(MyApplication.getAppContext());
        try {
            request.request(cursor,null, this);
        } catch (Exception e) {
            e.printStackTrace();
            request = null;
            notifyRefreshPost(null, 0, TextUtils.isEmpty(cursor), ErrorCode.ERROR_SUCCESS);
        }
    }

    @Override
    public void tryHisPosts() {
        if(request != null) {
            return;
        }

        actionState = GlobalConfig.LIST_ACTION_HIS;
        if (!TextUtils.isEmpty(cursor)) {
            request = new Hot24hFeedRequest(MyApplication.getAppContext());
            try {
                request.request(cursor,null, this);
            } catch (Exception e) {
                e.printStackTrace();
                request = null;
                notifyHisPost(null, false, ErrorCode.networkExceptionToErrCode(e));
            }
        } else {
            request = null;
            notifyHisPost(null, true, ErrorCode.ERROR_SUCCESS);
        }
    }

    @Override
    public void onError(BaseRequest request, int errCode) {
        if(request == this.request) {
            this.request = null;
            if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
                notifyRefreshPost(null, 0, TextUtils.isEmpty(cursor), errCode);
            } else if (actionState == GlobalConfig.LIST_ACTION_HIS) {
                notifyHisPost(null, false, errCode);
            }
        }
    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (request == this.request) {
            this.request = null;
            List<PostBase> posts = PostsDataSourceParser.parsePosts(result);
            cursor = PostsDataSourceParser.parseCursor(result);
            List<PostBase> list = filterPosts(posts);
            if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
                int newCount = refreshNewCount(list);
                cacheFirstPage(list);
                notifyRefreshPost(list, newCount, TextUtils.isEmpty(cursor), ErrorCode.ERROR_SUCCESS);
            } else if (actionState == GlobalConfig.LIST_ACTION_HIS) {
                if (!TextUtils.isEmpty(cursor)) {
                    notifyHisPost(list, false, ErrorCode.ERROR_SUCCESS);
                } else {
                    notifyHisPost(list, true, ErrorCode.ERROR_SUCCESS);
                }
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

    private void notifyRefreshPost(final List<PostBase> posts, final int newCount, final boolean last, final int errCode) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                if(listener != null) {
                    listener.onRefreshPosts(posts, newCount, last, errCode);
                }
            }
        });
    }

    private void notifyHisPost(final List<PostBase> posts, final boolean last, final int errCode) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                if(listener != null) {
                    listener.onReceiveHisPosts(posts, last, errCode);
                }
            }
        });
    }
}
