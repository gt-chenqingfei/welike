package com.redefine.welike.business.videoplayer.management.provider;

import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.provider.SinglePostsProviderCallback;

/**
 * Created by nianguowang on 2018/9/22
 */
public interface IVideoPostsProvider {

    void init(PostBase postBase, boolean auth);

    void tryRefreshPosts();

    void tryHisPosts();

    void setListener(SinglePostsProviderCallback callback);
}
