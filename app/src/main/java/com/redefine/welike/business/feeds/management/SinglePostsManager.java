package com.redefine.welike.business.feeds.management;

import com.redefine.foundation.framework.SingleListenerManagerBase;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.provider.ISinglePostsProvider;
import com.redefine.welike.business.feeds.management.provider.SinglePostsProviderCallback;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liubin on 2018/1/10.
 */

public class SinglePostsManager extends SingleListenerManagerBase implements SinglePostsProviderCallback {
    private ISinglePostsProvider dataSourceProvider;

    public interface PostsCallback {

        void onRefreshPosts(SinglePostsManager manager, List<PostBase> posts, int newCount, boolean last, int errCode);
        void onReceiveHisPosts(SinglePostsManager manager, List<PostBase> posts, boolean last, int errCode);

    }

    public SinglePostsManager() {}

    public void setDataSourceProvider(ISinglePostsProvider provider) {
        dataSourceProvider = provider;
    }

    public void setListener(PostsCallback listener) {
        super.setListener(listener);
        if (listener != null) {
            if (dataSourceProvider != null) {
                dataSourceProvider.setListener(this);
                dataSourceProvider.attachListener();
            }
        } else {
            if (dataSourceProvider != null) {
                dataSourceProvider.setListener(null);
                dataSourceProvider.detachListener();
            }
        }
    }

    public void tryRefreshPosts() {
        if (dataSourceProvider != null) {
            dataSourceProvider.tryRefreshPosts();
        }
    }

    public void tryHisPosts() {
        if (dataSourceProvider != null) {
            dataSourceProvider.tryHisPosts();
        }
    }

    @Override
    public void onRefreshPosts(final List<PostBase> posts, final int newCount, final boolean last, final int errCode) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                PostsCallback callback = getCallback();
                if (callback != null) {
                    callback.onRefreshPosts(SinglePostsManager.this, posts, newCount, last, errCode);
                }
            }

        });
    }

    @Override
    public void onReceiveHisPosts(final List<PostBase> posts, final boolean last, final int errCode) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                PostsCallback callback = getCallback();
                if (callback != null) {
                    callback.onReceiveHisPosts(SinglePostsManager.this, posts, last, errCode);
                }
            }

        });
    }

    private PostsCallback getCallback() {
        PostsCallback callback = null;
        Object l = getListener();
        if (l != null && l instanceof PostsCallback) {
            callback = (PostsCallback)l;
        }
        return callback;
    }

}
