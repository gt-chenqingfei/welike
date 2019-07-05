package com.redefine.commonui.share.shareapk;

import android.content.Context;
import android.content.Intent;

import com.redefine.commonui.share.AbsSharePackageManager;
import com.redefine.commonui.share.CommonListener;
import com.redefine.commonui.share.SharePackageFactory;
import com.redefine.commonui.share.sharemedel.ShareModel;

/**
 * Created by nianguowang on 2018/12/26
 */
public class ShareApkPackageManager extends AbsSharePackageManager {

    public ShareApkPackageManager(SharePackageFactory.SharePackage sharePackage) {
        super(sharePackage);
    }

    @Override
    public boolean isInstall(Context context) {
        return true;
    }

    @Override
    public boolean supportImage() {
        return false;
    }

    @Override
    public void shareTo(Context context, ShareModel shareModel, CommonListener<String> listener) {

    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {

    }
}
