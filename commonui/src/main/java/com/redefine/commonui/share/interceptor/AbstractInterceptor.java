package com.redefine.commonui.share.interceptor;

import com.redefine.commonui.share.ShareManager;
import com.redefine.commonui.share.SharePackageFactory;
import com.redefine.commonui.share.sharemedel.ShareModel;
import com.redefine.foundation.utils.LogUtil;

/**
 * Created by nianguowang on 2018/6/21
 */
public abstract class AbstractInterceptor {

    protected AbstractInterceptor mNext;
    protected IShowDialogCallback mCallback;

    public void doCurrent(ShareModel shareModel, SharePackageFactory.SharePackage channel) {
        ShareManager.getInstance().setCurrentInterceptor(this);
        LogUtil.d("wng_", "Current interceptor " + getClass().getName());
        handle(shareModel, channel);
    }

    public abstract void handle(ShareModel shareModel, SharePackageFactory.SharePackage channel);

    public void setNext(AbstractInterceptor interceptor) {
        mNext = interceptor;
    }

    public boolean hasNext() {
        return mNext != null;
    }

    public AbstractInterceptor next() {
        return mNext;
    }

    public void cancel() {
        mNext = null;
    }

    protected void doNext(ShareModel shareModel, SharePackageFactory.SharePackage channel) {
        if(mNext != null) {
            mNext.doCurrent(shareModel, channel);
        }
    }

    public void setShowDialogCallback(IShowDialogCallback callback) {
        this.mCallback = callback;
    }
}
