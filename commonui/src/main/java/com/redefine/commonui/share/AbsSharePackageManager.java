package com.redefine.commonui.share;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 *
 * Created by gongguan on 2018/3/20.
 */

public abstract class AbsSharePackageManager implements ISharePackageManager {
    private SharePackageFactory.SharePackage mSharePackage;

    public AbsSharePackageManager(SharePackageFactory.SharePackage sharePackage) {
        mSharePackage = sharePackage;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public SharePackageFactory.SharePackage getSharePackage() {
        return mSharePackage;
    }

    protected boolean isPkgInstalled(Context context, String pkgName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
//            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }
}
