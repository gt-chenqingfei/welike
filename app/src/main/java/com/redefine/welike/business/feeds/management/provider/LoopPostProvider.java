package com.redefine.welike.business.feeds.management.provider;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.common.PostsProviderBase;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.parser.PostsDataSourceParser;
import com.redefine.welike.business.feeds.management.request.LoopRequest;

import java.util.List;

/**
 * Created by mengnan on 2018/5/10.
 **/
public class LoopPostProvider extends PostsProviderBase implements ILoopPostProvider, RequestCallback {
    private LoopRequest loopRequest;
    private String cursor;
    private LoopPostsProviderCallBack listener;


    @Override
    public void onError(BaseRequest request, int errCode) {
        if (request == loopRequest) {
            loopRequest = null;
            if (listener != null) {
                listener.onGetNewPosts(null, false, errCode);
            }
        }

    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (request == loopRequest) {
            loopRequest = null;
            List<PostBase> posts = PostsDataSourceParser.parsePosts(result);
            cursor = PostsDataSourceParser.parseCursor(result);
            List<PostBase> list = filterPosts(posts);
                cacheFirstPage(list);
                if (listener != null) {
                    listener.onGetNewPosts(list, TextUtils.isEmpty(cursor), ErrorCode.ERROR_SUCCESS);
                }
        }

    }


    @Override
    public void LoopGetNewPost() {
        if (loopRequest != null) return;
        cursor = null;
        cacheList.clear();


        loopRequest = new LoopRequest(LoopRequest.HOME_FEEDS, true, MyApplication.getAppContext());
        try {
            loopRequest.tryFeeds(null, this);
        } catch (Exception e) {
            e.printStackTrace();
            loopRequest = null;
            if (listener != null) {
                listener.onGetNewPosts(null, false, ErrorCode.networkExceptionToErrCode(e));
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
    public void setListener(LoopPostsProviderCallBack callback) {
        this.listener = callback;
    }
}
