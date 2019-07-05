package com.redefine.foundation.io;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.redefine.foundation.utils.CollectionUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwenbo on 2018/3/12.
 */

public class FileCacheManager implements IFileCacheManager {

    private final Context mContext;

    public FileCacheManager(Context context) {
        mContext = context;
    }

    /**
     * 手机和应用清除缓存会触发删除的目录
     * @param fileName
     * @return
     */
    @Override
    public File getAndroidCachePath(String fileName) {
        File rootPath = null;
        if (TextUtils.equals(Environment.MEDIA_MOUNTED, Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            rootPath = mContext.getExternalCacheDir();
        }
        if (rootPath == null) {
            rootPath = mContext.getCacheDir();
        }
        checkDir(rootPath, fileName);
        return new File(rootPath, fileName);
    }

    @Override
    public File getAndroidCacheInnerPath(String fileName) {
        File rootPath = mContext.getCacheDir();
        checkDir(rootPath, fileName);
        return new File(rootPath, fileName);
    }

    @Override
    public List<File> getAndroidCacheListPath(String fileName) {
        List<File> files = new ArrayList<>();
        File rootPath;
        if (TextUtils.equals(Environment.MEDIA_MOUNTED, Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            rootPath = mContext.getExternalCacheDir();
            files.add(new File(rootPath, fileName));
        }
        rootPath = mContext.getCacheDir();
        files.add(new File(rootPath, fileName));
        return files;
    }

    /**
     * 手机和应用清除数据会触发删除的目录
     * @param fileName
     * @return
     */
    @Override
    public File getAndroidRootFile(String fileName) {
        File rootPath = null;
        boolean hasPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if (hasPermission && ((TextUtils.equals(Environment.MEDIA_MOUNTED, Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()))) {
            rootPath = Environment.getExternalStorageDirectory();
        }
        if (rootPath != null) {
            checkDir(rootPath, fileName);
            return new File(rootPath, fileName);
        }
        return null;
    }

    /**
     * 永久保留的目录
     * @param fileName
     * @return
     */
    @Override
    public File getAndroidFiles(String fileName) {
        File rootPath = null;
        if (TextUtils.equals(Environment.MEDIA_MOUNTED, Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            rootPath = mContext.getExternalFilesDir(null);
        }
        if (rootPath == null) {
            rootPath = mContext.getFilesDir();
        }
        checkDir(rootPath, fileName);

        return new File(rootPath, fileName);
    }

    /**
     * 获取可删除缓存的目录大小
     * @return
     * @param excludeDir
     */
    @Override
    public long getCacheSize(File excludeDir) {
        long size = 0;
        List<File> files = getCaches();
        if (CollectionUtil.isEmpty(files)) {
            return 0;
        }
        for (File file : files) {
            try {
                size += getFolderSize(file, excludeDir);
            } catch (Exception e) {
                // do nothing
            }
        }
        return size;
    }

    @Override
    public List<File> getCaches() {
        List<File> files = new ArrayList<>();
        if (TextUtils.equals(Environment.MEDIA_MOUNTED, Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            files.add(mContext.getExternalCacheDir());
        }
        files.add(mContext.getCacheDir());
        return files;
    }

    public boolean checkDir(File rootPath, String fileName) {
        File file = new File(rootPath, fileName);
        File parentFile = file.getParentFile();
        if (parentFile != null && !parentFile.exists()) {
            return parentFile.mkdirs();
        }
        return false;
    }


    private long getFolderSize(File file, final File excludeFile) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return !TextUtils.equals(pathname.getAbsolutePath(), excludeFile.getAbsolutePath());
                }
            });
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i], excludeFile);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            // do nothing
        }
        return size;
    }
}
