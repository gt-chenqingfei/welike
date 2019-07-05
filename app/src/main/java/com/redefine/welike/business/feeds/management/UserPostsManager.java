package com.redefine.welike.business.feeds.management;

import com.redefine.foundation.framework.BroadcastManagerBase;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.provider.UserPostsProvider;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liubin on 2018/1/11.
 */

public class UserPostsManager extends BroadcastManagerBase implements UserPostsProvider.UserPostsProviderCallback {
    private UserPostsProvider userPostsProvider;

    public interface UserPostsCallback {
        void onRefreshUserPosts(UserPostsManager manager, List<PostBase> posts, String uid, int newCount, int errCode);
        void onReceiveHisUserPosts(UserPostsManager manager, List<PostBase> posts, String uid, boolean last, int errCode);
    }

    public UserPostsManager() {
        userPostsProvider = new UserPostsProvider();
        userPostsProvider.setListener(this);
    }

    public void register(UserPostsCallback listener) {
        super.register(listener);
    }

    public void unregister(UserPostsCallback listener) {
        super.unregister(listener);
    }

    public void tryRefreshPosts(String uid, boolean isBrowse) {
        userPostsProvider.tryRefreshPosts(uid, isBrowse);
    }

    public void tryHisPosts(String uid, boolean isBrowse) {
        userPostsProvider.tryHisPosts(uid, isBrowse);
    }

    @Override
    public void onRefreshPosts(final List<PostBase> posts, final int newCount, final String uid, final int errCode) {
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
                if (l != null && l instanceof UserPostsCallback) {
                    UserPostsCallback listener = (UserPostsCallback)l;
                    listener.onRefreshUserPosts(this, posts, uid, newCount, errCode);
                }
            }
        }
    }

    private void broadcastHisFeeds(List<PostBase> posts, String uid, boolean last, int errCode) {
        synchronized (listenerRefList) {
            for (int i = 0; i < listenerRefList.size(); i++) {
                ListenerRefExt callbackRef = listenerRefList.get(i);
                Object l = callbackRef.getListener();
                if (l != null && l instanceof UserPostsCallback) {
                    UserPostsCallback listener = (UserPostsCallback)l;
                    listener.onReceiveHisUserPosts(this, posts, uid, last, errCode);
                }
            }
        }
    }

}
