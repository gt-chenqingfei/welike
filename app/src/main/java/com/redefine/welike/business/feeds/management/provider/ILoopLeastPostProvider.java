package com.redefine.welike.business.feeds.management.provider;

/**
 * Created by mengnan on 2018/5/10.
 **/
public interface ILoopLeastPostProvider {
    void LoopGetNewLeastPost();

    void attachListener();

    void detachListener();

    void setListener(LoopPostsLeastProviderCallBack callback);
}
