package com.redefine.commonui.share.interceptor;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.redefine.commonui.R;
import com.redefine.commonui.share.SharePackageFactory;
import com.redefine.commonui.share.sharemedel.ShareModel;
import com.redefine.welike.base.io.WeLikeFileManager;

import java.io.File;
import java.util.List;

import io.reactivex.schedulers.Schedulers;

/**
 * Created by nianguowang on 2018/12/26
 */
public class ShareApkInterceptor extends AbstractInterceptor {

    private Context mContext;
    public ShareApkInterceptor(Context context) {
        mContext = context;
    }

    @Override
    public void handle(final ShareModel shareModel, final SharePackageFactory.SharePackage channel) {
        Schedulers.newThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
//                ApplicationInfo applicationInfo = mContext.getApplicationInfo();
//                PackageManager packageManager = mContext.getPackageManager();
//                List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(PackageManager.GET_SIGNATURES);
//                for (ApplicationInfo application : installedApplications) {
//                    if (TextUtils.equals(application.packageName, mContext.getPackageName())) {
//                        String sourceDir = application.sourceDir;
//                        File targetFile = new File(getCacheFilePath(), getApkName());
//                        if (targetFile.exists()) {
//                            targetFile.delete();
//                        }
//                        File copy = WeLikeFileManager.copy(new File(sourceDir), new File(getCacheFilePath(), getApkName()));
//                        if (copy != null) {
//                            shareModel.setFilePath(copy.getPath());
//                        } else {
//                            shareModel.setFilePath(applicationInfo.sourceDir);
//                        }
//                        doNext(shareModel, channel);
//                        return;
//                    }
//                }
                ApplicationInfo application = mContext.getApplicationInfo();
                String sourceDir = application.sourceDir;
                File targetFile = new File(getCacheFilePath(), getApkName());
                if (targetFile.exists()) {
                    targetFile.delete();
                }
                File copy = WeLikeFileManager.copy(new File(sourceDir), new File(getCacheFilePath(), getApkName()));
                if (copy != null) {
                    shareModel.setFilePath(copy.getPath());
                } else {
                    shareModel.setFilePath(sourceDir);
                }
                doNext(shareModel, channel);
            }
        });
    }

    private File getCacheFilePath() {
        return WeLikeFileManager.getShareCacheDir();
    }

    private String getApkName() {
        return mContext.getString(R.string.share_apk_name) + WeLikeFileManager.APK_SUFFIX;
    }
}
