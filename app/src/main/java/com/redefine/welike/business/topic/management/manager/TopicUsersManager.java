package com.redefine.welike.business.topic.management.manager;

import com.redefine.foundation.framework.SingleListenerManagerBase;
import com.redefine.welike.business.topic.management.bean.TopicUser;
import com.redefine.welike.business.topic.management.provider.TopicUsersProvider;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liubin on 2018/3/26.
 */

public class TopicUsersManager extends SingleListenerManagerBase implements TopicUsersProvider.TopicUserProviderCallback {
    private final TopicUsersProvider provider;

    public interface TopicUsersCallback {

        void onRefreshTopicUsers(List<TopicUser> users, int errCode);

        void onReceiveHisTopicUsers(List<TopicUser> users, boolean last, int errCode);

    }

    public TopicUsersManager(String topicId) {
        provider = new TopicUsersProvider(topicId);
        provider.setListener(this);
    }

    public void setListener(TopicUsersCallback listener) {
        super.setListener(listener);
    }

    public void tryRefreshUsers() {
        provider.tryRefreshUsers();
    }

    public void tryHisUsers() {
        provider.tryHisUsers();
    }

    @Override
    public void onRefreshTopicUsers(final List<TopicUser> users, final int errCode) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                TopicUsersCallback callback = getCallback();
                if (callback != null) {
                    callback.onRefreshTopicUsers(users, errCode);
                }
            }

        });
    }

    @Override
    public void onReceiveHisTopicUsers(final List<TopicUser> users, final boolean last, final int errCode) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                TopicUsersCallback callback = getCallback();
                if (callback != null) {
                    callback.onReceiveHisTopicUsers(users, last, errCode);
                }
            }

        });
    }

    private TopicUsersCallback getCallback() {
        TopicUsersCallback callback = null;
        Object l = getListener();
        if (l != null && l instanceof TopicUsersCallback) {
            callback = (TopicUsersCallback)l;
        }
        return callback;
    }

}
