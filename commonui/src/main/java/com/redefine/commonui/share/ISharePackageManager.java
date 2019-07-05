package com.redefine.commonui.share;

import android.content.Context;
import android.content.Intent;

import com.redefine.commonui.share.sharemedel.ShareModel;

/**
 * Created by gongguan on 2018/3/20.
 */

public interface ISharePackageManager {
    boolean isInstall(Context context);

    SharePackageFactory.SharePackage getSharePackage();

    boolean supportImage();

    void shareTo(Context context, ShareModel shareModel, CommonListener<String> listener);

    void onActivityResult(int requestCode, int responseCode, Intent intent);

    void onDestroy();
}
