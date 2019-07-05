package com.redefine.welike.business.feeds.management;

import com.redefine.foundation.framework.BroadcastManagerBase;
import com.redefine.welike.business.feeds.management.bean.PollItemInfo;
import com.redefine.welike.business.feeds.management.provider.UserVoteProvider;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by mengnan on 2018/5/12.
 **/
public class UserVoteManager extends BroadcastManagerBase implements UserVoteProvider.UserVoteProviderCallback {
    private UserVoteProvider userVoteProvider = new UserVoteProvider();


    private static class UserVoteManagerHolder {
        public static UserVoteManager instance = new UserVoteManager();
    }

    public interface UserVoteCallback {
        void onUserVotePosts(Object o, int errCode);
    }

    public static UserVoteManager getInstance() {
        return UserVoteManagerHolder.instance;
    }

    public void register(UserVoteCallback listener) {
        super.register(listener);
        userVoteProvider.setListener(this);
    }

    public void unregister(UserVoteCallback listener) {
        super.unregister(listener);
        userVoteProvider.setListener(null);
    }

    public void tryVote(String pid, String pollId, ArrayList<PollItemInfo> choiceIds, boolean isRepost) {
        userVoteProvider.tryVote(pid, pollId, choiceIds, isRepost);
    }

    @Override
    public void onRefreshVote(final Object pollInfo, final int errCode) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                broadcastRefreshPosts(pollInfo, errCode);
            }

        });
    }


    private void broadcastRefreshPosts(Object pollInfo, int errCode) {
        synchronized (listenerRefList) {
            for (int i = 0; i < listenerRefList.size(); i++) {
                ListenerRefExt callbackRef = listenerRefList.get(i);
                Object l = callbackRef.getListener();
                if (l != null && l instanceof UserVoteCallback) {
                    UserVoteCallback listener = (UserVoteCallback) l;
                    listener.onUserVotePosts(pollInfo, errCode);
                }
            }
        }
    }
}
