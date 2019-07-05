package com.redefine.welike.business.user.management;

import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.message.management.request.NotificationsRequest;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by daining on 2018/4/2.
 */

public enum FollowerManager {

    INSTANCE;

    final private String type = "GENERAL";

    private int follower = 0;

    private ArrayList<GetFollowersCallback> callbacks = new ArrayList<>();

    private ReentrantLock lock = new ReentrantLock();

    public void regCallback(GetFollowersCallback callback) {
        lock.lock();
        try {
            if (!callbacks.contains(callback)) {
                callbacks.add(callback);
            }
        } finally {
            lock.unlock();
        }
    }

    public void unregCallback(GetFollowersCallback callback) {
        lock.lock();
        try {
            callbacks.remove(callback);
        } finally {
            lock.unlock();
        }
    }

    public void requestNews() {
        callback();
        try {
            new FollowerRequest(1, MyApplication.getAppContext()).req(new RequestCallback() {

                @Override
                public void onError(BaseRequest request, int errCode) {
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) {
                    follower = result.getIntValue(type);
                    callback();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setRead() {
        follower = 0;
        callback();
        try {
            new NotificationsRequest(type, MyApplication.getAppContext()).refresh(new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {

                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callback() {
        lock.lock();
        try {
            for (final GetFollowersCallback callback : callbacks) {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onNewFollowers(follower);
                        }
                    }
                });
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取 new followers 数量。
     */
    public interface GetFollowersCallback {

        void onNewFollowers(int count);
    }
}
