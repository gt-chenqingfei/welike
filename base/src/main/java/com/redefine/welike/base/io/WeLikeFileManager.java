package com.redefine.welike.base.io;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.redefine.foundation.io.FileManager;

import java.io.File;
import java.io.InputStream;

/**
 * Created by liwenbo on 2018/3/12.
 */

public class WeLikeFileManager {

    public static final String TEMP_CACHE_DIR = "/temp/";  // 修改为只在重新进入APP清除当前的缓存目录
    public static final String WEBVIEW_CACHE_DIR = "/web/";
    public static final String FRESCO_CACHE_DIR = "/fresco/";
    public static final String SHARE_CACHE_DIR = "/share/";
    public static final String SHARE_PIC_CACHE_DIR = "/share/picture/";
    public static final String SHARE_VIDEO_CACHE_DIR = "/share/video/";
    public static final String PHOTO_SAVE_DIR = "/Welike/Photo/";
    @Deprecated
    public static final String PUBLISH_UPLOAD_DIR = "/Upload/";  // TODO  需在一版强制升级删除此目录

    public static final String APP_NAME = "Welike";
    private static final String VIDEO_COVER_SUFFIX = "video_";
    public static final String JPG_TMP_SUFFIX = ".jpg.tmp";
    public static final String MP4_TMP_SUFFIX = ".mp4.tmp";
    public static final String JPG_SUFFIX = ".jpg";
    public static final String TMP_SUFFIX = ".tmp";
    public static final String APK_SUFFIX = ".apk";
    public static final String WELIKE_PREFFIX = "Welike_";

    public static File getTempCacheDir() {
        return FileManager.getInstance().getFileInAndroidCache(TEMP_CACHE_DIR);
    }

    public static File getFrescoCacheDir() {
        return FileManager.getInstance().getFileInAndroidCache(FRESCO_CACHE_DIR);
    }

    public static File getShareCacheDir() {
        return FileManager.getInstance().getFileInAndroidInnerCache(SHARE_CACHE_DIR);
    }

    public static File getSharePicCacheDir() {
        return FileManager.getInstance().getFileInAndroidInnerCache(SHARE_PIC_CACHE_DIR);
    }

    public static File getShareVideoCacheDir() {
        return FileManager.getInstance().getFileInAndroidInnerCache(SHARE_VIDEO_CACHE_DIR);
    }

    public static File copyToTempCache(File source, String target) {
        if (TextUtils.isEmpty(target)) {
            return source;
        }
        if (!target.endsWith(TMP_SUFFIX)) {
            return FileManager.getInstance().copyToAndroidCache(source, TEMP_CACHE_DIR + target + TMP_SUFFIX);
        }
        return FileManager.getInstance().copyToAndroidCache(source, TEMP_CACHE_DIR + target);
    }

    public static File getTempCacheFile(String fileName) {
        File path = getTempCacheDir();
        if (path != null && !path.exists()) {
            path.mkdirs();
        }
        return new File(path, fileName);
    }

    public static File getPhotoSaveFile(String fileName) {
        File path = FileManager.getInstance().getFileInSDRoot(PHOTO_SAVE_DIR);
        if (path != null && !path.exists()) {
            path.mkdirs();
        }
        if (path != null) {
            return new File(path, fileName);
        }
        return null;
    }

    /**
     * 清除图片压缩的临时目录
     *
     * @return
     */
    public static void clearTempCacheDir() {
        FileManager.getInstance().clearFilesInAndroidCache(TEMP_CACHE_DIR);
    }

    /**
     * 清除整体APP的临时cache目录
     *
     * @return
     */
    public static boolean clearAppCacheDir() {
        return FileManager.getInstance().clearAppCache();
    }

    /**
     * 清除整体APP的临时cache目录
     * Fresco的缓存目录通过fresco单独获取，故屏蔽掉Fresco目录
     *
     * @return
     */
    public static long getAppCacheSize() {
        return FileManager.getInstance().getAppCacheSize(getFrescoCacheDir());
    }

    public static void refreshGallery(Context context, String dirPath) {
        if (new File(dirPath).exists()) {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(dirPath))));
        }
    }

    public static File saveToTempCache(Bitmap bitmap, String fileName) {
        boolean isSuccess = FileManager.getInstance().writeBitmapInAndroidCache(TEMP_CACHE_DIR + fileName, bitmap);

        if (isSuccess) {
            return FileManager.getInstance().getFileInAndroidCache(TEMP_CACHE_DIR + fileName);
        }
        return null;
    }

    public static File copyToPhotoSaveDir(File source, String target) {
        if (TextUtils.isEmpty(target)) {
            return null;
        }
        return FileManager.getInstance().copyToSDPath(source, PHOTO_SAVE_DIR + target);
    }

    public static boolean deleteFile(String filePath) {
        return new File(filePath).delete();
    }

    public static String parseTmpFileSuffix(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            String fileName = file.getName();
            String name[] = fileName.split("\\.");
            String suffix = "";
            if (name.length > 2) {
                suffix = name[name.length - 2];
            } else if (name.length > 1) {
                suffix = name[name.length - 1];
            }
            return suffix;
        }
        return null;
    }

    public static String getWebViewCacheDir() {
        return FileManager.getInstance().getFileInAndroidCache(WEBVIEW_CACHE_DIR).getAbsolutePath();
    }

    public static File saveToPhotoSaveDir(Bitmap bitmap, String fileName, boolean isRecycleBitmap) {
        boolean isSuccess = FileManager.getInstance().writeFileInSDRoot(PHOTO_SAVE_DIR + fileName, bitmap, isRecycleBitmap);

        if (isSuccess) {
            return FileManager.getInstance().getFileInSDRoot(PHOTO_SAVE_DIR + fileName);
        }
        return null;
    }

    public static File saveToPhotoSaveDir(InputStream inputStream, String fileName) {
        boolean isSuccess = FileManager.getInstance().writeStreamInSDRoot(PHOTO_SAVE_DIR + fileName, inputStream);

        if (isSuccess) {
            return FileManager.getInstance().getFileInSDRoot(PHOTO_SAVE_DIR + fileName);
        }
        return null;
    }

    public static void saveFrontCameraBitmap(String mFilePath, byte[] data) {
        FileManager.getInstance().saveFrontCameraBitmap(mFilePath, data);
    }

    public static void saveBackgroundCameraBitmap(String mFilePath, byte[] data) {
        FileManager.getInstance().saveBackgroundCameraBitmap(mFilePath, data);
    }

    public static void saveBitmap(File tagImg, Bitmap rotateBitmap) {
        FileManager.getInstance().saveBitmap(tagImg, rotateBitmap);
    }


    public static void notifySystemMedia(File file, Context context) {
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), null);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(file.getPath()))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Bitmap readBitmap(File result) {
        return FileManager.getInstance().readBitmap(result);
    }

    public static File copy(File file, File imageCacheFile) {
        return FileManager.getInstance().copy(file, imageCacheFile);
    }

    /**
     * 是否是网络图片
     *
     * @param path
     * @return
     */
    public static boolean isHttp(String path) {
        if (!TextUtils.isEmpty(path)) {
            if (path.startsWith("http")
                    || path.startsWith("https")) {
                return true;
            }
        }
        return false;
    }


}
