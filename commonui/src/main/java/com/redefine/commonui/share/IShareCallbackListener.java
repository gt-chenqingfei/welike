package com.redefine.commonui.share;

/**
 * Created by gongguan on 2018/3/20.
 */

public interface IShareCallbackListener {
    void shareCompleted(SharePackageFactory.SharePackage sharePackage);

    void shareFailed(SharePackageFactory.SharePackage sharePackage);

    void shareCanceled();
}
