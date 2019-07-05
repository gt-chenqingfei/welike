package com.redefine.foundation.io;

import android.content.Context;
import android.graphics.Bitmap;

import com.redefine.foundation.utils.CollectionUtil;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * Created by liwenbo on 2018/3/12.
 */

public class FileManager {

    private static Context mContext;
    private final IFileCacheManager mFileCacheManager;
    private final FileStreamManager mFileStreamManager;

    private FileManager() {
        if (mContext == null) {
            throw new RuntimeException("you must call init before getInstance");
        }
        mFileCacheManager = FileCacheManagerFactory.getDefaultCacheManager(mContext);
        mFileStreamManager = new FileStreamManager();
    }

    private static class FileManagerHolder {
        private static FileManager INSTANCE = new FileManager();
        private FileManagerHolder() {

        }
    }

    public static FileManager getInstance() {
        return FileManagerHolder.INSTANCE;
    }

    public static void init(Context context) {
        mContext = context;
    }


    /**
     * 读Android caches目录的文件，包含内置sd卡和外置sd卡
     * 优先读外置sd卡，没有外置sd卡从内置sd卡读
     * @param fileName  文件名称例如: filePath/fileName
     * @return 返回读取的二进制数据
     */
    public byte[] readFileInAndroidCache(String fileName) {
        File file = mFileCacheManager.getAndroidCachePath(fileName);
        return mFileStreamManager.readFile(file);
    }

    public boolean writeBitmapInAndroidCache(String fileName, Bitmap bitmap) {
        File file = mFileCacheManager.getAndroidCachePath(fileName);
        return mFileStreamManager.writeBitmap(file, bitmap);
    }

    public boolean writeBitmapInAndroidFile(String fileName, Bitmap bitmap, boolean isRecycleBitmap) {
        File file = mFileCacheManager.getAndroidFiles(fileName);
        return mFileStreamManager.writeBitmap(file, bitmap, isRecycleBitmap);
    }

    public boolean writeBitmapInInnerAndroidCache(String fileName, Bitmap bitmap) {
        File file = mFileCacheManager.getAndroidCachePath(fileName);
        return mFileStreamManager.writeBitmap(file, bitmap);
    }

    public boolean writeFileInSDRoot(String fileName, Bitmap bitmap, boolean isRecycleBitmap) {
        File file = mFileCacheManager.getAndroidRootFile(fileName);
        return mFileStreamManager.writeBitmap(file, bitmap, isRecycleBitmap);
    }

    public boolean writeStreamInSDRoot(String fileName, InputStream stream) {
        File file = mFileCacheManager.getAndroidRootFile(fileName);
        return mFileStreamManager.writeStream(file, stream);
    }

    /**
     * 写入Android caches目录的文件，包含内置sd卡和外置sd卡
     * 优先读外置sd卡，没有外置sd卡从内置sd卡读
     * @param fileName  文件名称例如: filePath/fileName
     * @param bytes 需要写入的文件二进制数据
     * @return 是否写入成功
     */
    public boolean writeFileInAndroidCache(String fileName, byte[] bytes) {
        File file = mFileCacheManager.getAndroidCachePath(fileName);
        return mFileStreamManager.writeFile(file, bytes);
    }

    /**
     * 获取Android caches目录的文件，包含内置sd卡和外置sd卡
     * 优先读外置sd卡，没有外置sd卡从内置sd卡读
     * @param fileName  文件名称例如: filePath/fileName
     * @return 文件目录
     */
    public File getFileInAndroidCache(String fileName) {
        return mFileCacheManager.getAndroidCachePath(fileName);
    }

    public File getFileInAndroidInnerCache(String fileName) {
        return mFileCacheManager.getAndroidCacheInnerPath(fileName);
    }

    /**
     * 清除Android  cache
     */
    public boolean clearAppCache() {
        List<File> files = mFileCacheManager.getCaches();
        if (CollectionUtil.isEmpty(files)) {
            return false;
        }
        for (File file : files) {
            if (file != null) {
                mFileStreamManager.deleteFiles(file);
            }
        }
        return true;
    }

    public long getAppCacheSize(File excludeDir) {
        return mFileCacheManager.getCacheSize(excludeDir);
    }

    /**
     * 获取Android caches目录的文件，包含内置sd卡和外置sd卡
     * 优先读外置sd卡，没有外置sd卡从内置sd卡读
     * @param fileName  文件名称例如: filePath/fileName
     * @return 是否存在
     */
    public boolean isFileInAndroidCache(String fileName) {
        File file = mFileCacheManager.getAndroidCachePath(fileName);
        return file != null && file.exists();
    }

    /**
     * 读外置sd卡根目录的文件
     * @param fileName  文件名称例如: filePath/fileName
     * @return 读取到的二进制流
     */
    public byte[] readFileInSDRoot(String fileName) {
        File file = mFileCacheManager.getAndroidRootFile(fileName);
        return mFileStreamManager.readFile(file);
    }

    /**
     * 外置sd卡文件写入
     * @param fileName  文件名称例如: filePath/fileName
     * @param bytes 需要写入的文件二进制数据
     * @return 是否写入成功
     */
    public boolean writeFileInSDRoot(String fileName, byte[] bytes) {
        File file = mFileCacheManager.getAndroidRootFile(fileName);
        return mFileStreamManager.writeFile(file, bytes);
    }

    /**
     * 外置sd卡文件目录
     * @param fileName  文件名称例如: filePath/fileName
     * @return 文件目录
     */
    public File getFileInSDRoot(String fileName) {
        return mFileCacheManager.getAndroidRootFile(fileName);
    }

    /**
     * 外置sd卡文件目录是否存在
     * @param fileName  文件名称例如: filePath/fileName
     * @return 是否存在
     */
    public boolean isFileInSDRoot(String fileName) {
        File file = mFileCacheManager.getAndroidRootFile(fileName);
        return file != null && file.exists();
    }

    public File copyToSDPath(File source, String fileName) {
        File file = mFileCacheManager.getAndroidRootFile(fileName);
        return mFileStreamManager.copyFileToFile(source, file);
    }

    /**
     * 读Android files目录的文件，包含内置sd卡和外置sd卡
     * 优先读外置sd卡，没有外置sd卡从内置sd卡读
     * @param fileName  文件名称例如: filePath/fileName
     * @return 返回读取的二进制数据
     */
    public byte[] readFileInAndroidFile(String fileName) {
        File file = mFileCacheManager.getAndroidFiles(fileName);
        return mFileStreamManager.readFile(file);
    }

    /**
     * 写入Android files目录的文件，包含内置sd卡和外置sd卡
     * 优先读外置sd卡，没有外置sd卡从内置sd卡读
     * @param fileName  文件名称例如: filePath/fileName
     * @param bytes 需要写入的文件二进制数据
     * @return 是否写入成功
     */
    public boolean writeFileInAndroidFile(String fileName, byte[] bytes) {
        File file = mFileCacheManager.getAndroidFiles(fileName);
        return mFileStreamManager.writeFile(file, bytes);
    }

    public File copyToAndroidCache(File source, String fileName) {
        File file = mFileCacheManager.getAndroidCachePath(fileName);
        return mFileStreamManager.copyFileToFile(source, file);
    }

    public void writeFile(String mFilePath, byte[] data) {
        mFileStreamManager.writeFile(new File(mFilePath), data);
    }

    public void saveFrontCameraBitmap(String mFilePath, byte[] data) {
        mFileStreamManager.saveFrontCameraBitmap(new File(mFilePath), data);
    }

    public void saveBackgroundCameraBitmap(String mFilePath, byte[] data) {
        mFileStreamManager.saveBackgroundCameraBitmap(new File(mFilePath), data);
    }

    public File copyToAndroidFiles(File source, String fileName) {
        File file = mFileCacheManager.getAndroidFiles(fileName);
        return mFileStreamManager.copyFileToFile(source, file);
    }

    public File copy(File source, File file) {
        return mFileStreamManager.copyFileToFile(source, file);
    }

    /**
     * 获取Android files目录的文件，包含内置sd卡和外置sd卡
     * 优先读外置sd卡，没有外置sd卡从内置sd卡读
     * @param fileName  文件名称例如: filePath/fileName
     * @return 文件目录
     */
    public File getFileInAndroidFile(String fileName) {
        return mFileCacheManager.getAndroidFiles(fileName);
    }

    /**
     * 获取Android files目录的文件，包含内置sd卡和外置sd卡
     * 优先读外置sd卡，没有外置sd卡从内置sd卡读
     * @param fileName  文件名称例如: filePath/fileName
     * @return 是否存在
     */
    public boolean isFileInAndroidFiles(String fileName) {
        File file = mFileCacheManager.getAndroidFiles(fileName);
        return file != null && file.exists();
    }

    /**
     * 清除AndroidCache此目录下的文件cache
     * @param fileName
     * @return
     */
    public void clearFilesInAndroidCache(String fileName) {
        List<File> files = mFileCacheManager.getAndroidCacheListPath(fileName);
        if (CollectionUtil.isEmpty(files)) {
            return ;
        }
        for (File file : files) {
            if (file != null) {
                mFileStreamManager.deleteFiles(file);
            }
        }
    }

    public void saveBitmap(File tagImg, Bitmap rotateBitmap) {
        mFileStreamManager.writeBitmap(tagImg, rotateBitmap);
    }

    public Bitmap readBitmap(File result) {
        return mFileStreamManager.readBitmap(result);
    }
}
