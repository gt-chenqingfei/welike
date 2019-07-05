package com.redefine.welike.business.user.management;

import com.redefine.foundation.framework.BroadcastManagerBase;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.user.management.provider.UserLikePostsProvider;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liubin on 2018/2/26.
 */

public class UserLikePostsManager extends BroadcastManagerBase implements UserLikePostsProvider.UserLikePostsProviderCallback {
    private UserLikePostsProvider userLikePostsProvider;

    public interface UserLikePostsCallback {
        void onRefreshUserLikePosts(UserLikePostsManager manager, List<PostBase> posts, String uid, int newCount, int errCode);
        void onReceiveHisUserLikePosts(UserLikePostsManager manager, List<PostBase> posts, String uid, boolean last, int errCode);
    }

    public UserLikePostsManager() {
        userLikePostsProvider = new UserLikePostsProvider();
        userLikePostsProvider.setListener(this);
    }

    public void register(UserLikePostsCallback listener) {
        super.register(listener);
    }

    public void unregister(UserLikePostsCallback listener) {
        super.unregister(listener);
    }

    public void tryRefreshPosts(String uid, boolean isBrowse) {
        userLikePostsProvider.tryRefreshPosts(uid, isBrowse);
    }

    public void tryHisPosts(String uid, boolean isBrowse) {
        userLikePostsProvider.tryHisPosts(uid, isBrowse);
    }

    @Override
    public void onRefreshPosts(final List<PostBase> posts, final String uid, final int newCount, final int errCode) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                broadcastRefreshPosts(posts, uid, newCount, errCode);
            }

        });
    }

    @Override
    public void onReceiveHisPosts(final List<PostBase> posts, final String uid, final boolean last, final int errCode) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                broadcastHisFeeds(posts, uid, last, errCode);
            }

        });
    }

    private void broadcastRefreshPosts(List<PostBase> posts, String uid, int newCount, int errCode) {
        synchronized (listenerRefList) {
            for (int i = 0; i < listenerRefList.size(); i++) {
                ListenerRefExt callbackRef = listenerRefList.get(i);
                Object l = callbackRef.getListener();
                if (l != null && l instanceof UserLikePostsCallback) {
                    UserLikePostsCallback listener = (UserLikePostsCallback)l;
                    listener.onRefreshUserLikePosts(this, posts, uid, newCount, errCode);
                }
            }
        }
    }

    private void broadcastHisFeeds(List<PostBase> posts, String uid, boolean last, int errCode) {
        synchronized (listenerRefList) {
            for (int i = 0; i < listenerRefList.size(); i++) {
                ListenerRefExt callbackRef = listenerRefList.get(i);
                Object l = callbackRef.getListener();
                if (l != null && l instanceof UserLikePostsCallback) {
                    UserLikePostsCallback listener = (UserLikePostsCallback)l;
                    listener.onReceiveHisUserLikePosts(this, posts, uid, last, errCode);
                }
            }
        }
    }

}
