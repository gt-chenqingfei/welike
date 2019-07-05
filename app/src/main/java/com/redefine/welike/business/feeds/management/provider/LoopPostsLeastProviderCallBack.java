package com.redefine.welike.business.feeds.management.provider;

import com.redefine.welike.business.feeds.management.bean.PostBase;

import java.util.List;

/**
 * Created by mengnan on 2018/5/10.
 **/
public interface LoopPostsLeastProviderCallBack {
    void onGetNewLeastPosts(final List<PostBase> posts, boolean last, final int errCode);
}
