package com.redefine.commonui.share.sharemedel;

import com.redefine.commonui.R;
import com.redefine.commonui.share.EmptyMenuShareManager;
import com.redefine.commonui.share.ISharePackageManager;
import com.redefine.commonui.share.SharePackageFactory;

/**
 * Created by nianguowang on 2018/8/31
 */
public class SharePackageModel {

    private String packageName;
    private int packageIconResId;
    private SharePackageFactory.SharePackage sharePackage;
    private ISharePackageManager listener;

    public SharePackageModel(String packageName, int packageIconResId, SharePackageFactory.SharePackage sharePackage, ISharePackageManager listener) {
        this.packageName = packageName;
        this.packageIconResId = packageIconResId;
        this.sharePackage = sharePackage;
        this.listener = listener;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getPackageIconResId() {
        return packageIconResId;
    }

    public void setPackageIconResId(int packageIconResId) {
        this.packageIconResId = packageIconResId;
    }

    public SharePackageFactory.SharePackage getSharePackage() {
        return sharePackage;
    }

    public void setSharePackage(SharePackageFactory.SharePackage sharePackage) {
        this.sharePackage = sharePackage;
    }

    public ISharePackageManager getListener() {
        return listener;
    }

    public void setListener(ISharePackageManager listener) {
        this.listener = listener;
    }

}
