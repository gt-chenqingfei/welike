package com.redefine.foundation.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by liubin on 2018/1/5.
 */

public class LocalPathHelper {
    private static String sDocLocalPath = null;

    public static String cacheLocalPath(String subName, Context context) {
        String cacheLocalPath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                !Environment.isExternalStorageRemovable()) {
            File externalCacheDir = context.getExternalCacheDir();
            if (externalCacheDir != null) {
                if (subName != null && subName.length() > 0) {
                    cacheLocalPath = externalCacheDir.getPath() + File.separator + subName;
                } else {
                    cacheLocalPath = externalCacheDir.getPath();
                }
                if (!TextUtils.isEmpty(cacheLocalPath)) {
                    File cacheDir = new File(cacheLocalPath);
                    if (!cacheDir.exists()) {
                        cacheDir.mkdirs();
                    }
                }
                return cacheLocalPath;
            }

        }

        if (subName != null && subName.length() > 0) {
            cacheLocalPath = context.getCacheDir().getPath() + File.separator + subName;
        } else {
            cacheLocalPath = context.getCacheDir().getPath();
        }
        if (!TextUtils.isEmpty(cacheLocalPath)) {
            File cacheDir = new File(cacheLocalPath);
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
        }
        return cacheLocalPath;
    }

    public static String internalFilePath(String subName, Context context) {
        String internalPath;
        if (subName != null && subName.length() > 0) {
            internalPath = context.getFilesDir().getPath() + File.separator + subName;
        } else {
            internalPath = context.getFilesDir().getPath();
        }
        File fileDir = new File(internalPath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return internalPath;
    }

    public static String localPath(String subName) {
        if (sDocLocalPath == null) {
            sDocLocalPath = baseLocalPath() + subName;
            File docDir = new File(sDocLocalPath);
            if (!docDir.exists()) {
                docDir.mkdirs();
            }
        }
        return sDocLocalPath;
    }

    private static String baseLocalPath() {
        String appBasePath = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            appBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "welike" + File.separator;
        } else {
            String mntPath = File.separator + "mnt";
            String mountPath = File.separator + "mount";
            if (new File(mntPath).exists()) {
                String baseExtSdCardPath = mntPath + File.separator + "extSdCard";
                if (new File(baseExtSdCardPath).exists()) {
                    appBasePath = baseExtSdCardPath + File.separator;
                }
            } else if (new File(mountPath).exists()) {
                String baseExtSdCardPath = mountPath + File.separator + "extSdCard";
                if (new File(baseExtSdCardPath).exists()) {
                    appBasePath = baseExtSdCardPath + File.separator;
                }
            }
        }
        return appBasePath;
    }

}
