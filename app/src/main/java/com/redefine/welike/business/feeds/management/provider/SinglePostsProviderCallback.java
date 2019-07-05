package com.redefine.welike.business.feeds.management.provider;

import com.redefine.welike.business.feeds.management.bean.PostBase;

import java.util.List;

/**
 * Created by liubin on 2018/1/10.
 */

public interface SinglePostsProviderCallback {

    void onRefreshPosts(final List<PostBase> posts, final int newCount, boolean last, final int errCode);

    void onReceiveHisPosts(final List<PostBase> posts, final boolean last, final int errCode);

}
