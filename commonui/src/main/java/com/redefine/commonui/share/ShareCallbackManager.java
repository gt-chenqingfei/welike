package com.redefine.commonui.share;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by gongguan on 2018/3/21.
 */

public class ShareCallbackManager {
    private static ShareCallbackManager instance;
    private List<IShareCallbackListener> mCallbackListener = new CopyOnWriteArrayList<>();

    public ShareCallbackManager() {
    }

    public static ShareCallbackManager getInstance() {
        if (instance == null) {
            synchronized (ShareCallbackManager.class) {
                if (instance == null) {
                    instance = new ShareCallbackManager();
                }
            }
        }
        return instance;
    }

    public void registerShareCallback(IShareCallbackListener shareCallback) {
        if (shareCallback != null && !mCallbackListener.contains(shareCallback)) {
            mCallbackListener.add(shareCallback);
        }
    }

    public void unRegisterShareCallback(IShareCallbackListener shareCallback) {
        if (shareCallback != null && mCallbackListener.contains(shareCallback)) {
            mCallbackListener.remove(shareCallback);
        }
    }

    public void clearShareCallbacks() {
        mCallbackListener.clear();
    }

    public void notifySuccess(SharePackageFactory.SharePackage sp) {
        for (IShareCallbackListener callback : mCallbackListener) {
            callback.shareCompleted(sp);
        }
    }

    public void notifyFail(SharePackageFactory.SharePackage sp) {
        for (IShareCallbackListener callback : mCallbackListener) {
            callback.shareFailed(sp);
        }
    }

    public void notifyCancel() {
        for (IShareCallbackListener callback : mCallbackListener) {
            callback.shareCanceled();
        }
    }
}
