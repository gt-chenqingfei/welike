package com.redefine.welike.business.feeds.management.provider;

/**
 * Created by liubin on 2018/1/10.
 */

public interface ISinglePostsProvider {

    void tryRefreshPosts();

    void tryHisPosts();

    void attachListener();

    void detachListener();

    void setListener(SinglePostsProviderCallback callback);

}
