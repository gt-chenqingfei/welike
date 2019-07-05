package com.redefine.commonui.share;

import android.content.Context;
import android.content.Intent;

import com.redefine.commonui.share.sharemedel.ShareModel;

/**
 * Created by nianguowang on 2018/8/31
 */
public abstract class DefaultShareMenuManager extends AbsSharePackageManager {

    public DefaultShareMenuManager(SharePackageFactory.SharePackage sharePackage) {
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
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {

    }

    @Override
    public void shareTo(Context context, ShareModel shareModel, CommonListener<String> listener) {

    }
}
