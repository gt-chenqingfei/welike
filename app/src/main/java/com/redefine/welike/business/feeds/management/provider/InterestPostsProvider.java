package com.redefine.welike.business.feeds.management.provider;

import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.common.PostsProviderBase;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mengnan on 2018/6/13.
 **/
public class InterestPostsProvider extends PostsProviderBase implements ISinglePostsProvider, RequestCallback {
    Map<String,String>cursorMap=new ConcurrentHashMap<>();
    @Override
    public void onError(BaseRequest request, int errCode) {

    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {

    }

    @Override
    public void tryRefreshPosts() {

    }

    @Override
    public void tryHisPosts() {

    }

    @Override
    public void attachListener() {

    }

    @Override
    public void detachListener() {

    }

    @Override
    public void setListener(SinglePostsProviderCallback callback) {

    }


    public void setInterestId(String id){

    }
}
