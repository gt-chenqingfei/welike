package com.redefine.welike.business.feeds.management.provider;

/**
 * Created by mengnan on 2018/5/10.
 **/
public interface ILoopPostProvider {

    void LoopGetNewPost();

    void attachListener();

    void detachListener();

    void setListener(LoopPostsProviderCallBack callback);
}
