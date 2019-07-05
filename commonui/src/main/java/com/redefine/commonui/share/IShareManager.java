package com.redefine.commonui.share;

import android.content.Context;
import android.content.Intent;

import com.redefine.commonui.share.sharemedel.ShareModel;

/**
 * Created by gongguan on 2018/3/20.
 */

public interface IShareManager {
    boolean isInstall(Context context, SharePackageFactory.SharePackage sharePackage);

    void shareTo(Context context, ShareModel shareModel, SharePackageFactory.SharePackage sharePackage
            , CommonListener<String> listener);

    void onDestroy();

    void onActivityResult(int requestCode, int responseCode, Intent intent);
}
