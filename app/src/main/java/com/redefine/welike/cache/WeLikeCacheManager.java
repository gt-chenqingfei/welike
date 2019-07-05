package com.redefine.welike.cache;

import android.content.Context;
import android.text.format.Formatter;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.redefine.welike.base.io.WeLikeFileManager;

/**
 * Created by liwenbo on 2018/3/13.
 */

public class WeLikeCacheManager {

    public static String getCacheSizeShow(Context context) {
        long frescoSize = Fresco.getImagePipelineFactory().getMainFileCache().getSize();
        long extraSize = WeLikeFileManager.getAppCacheSize();
        long total = Math.max(0, frescoSize) + extraSize;
        return formatFileSize(context, total);
    }

    public static String formatFileSize(Context context, long total) {
        return Formatter.formatFileSize(context, total);
    }


    public static void clearAppCache() {
        Fresco.getImagePipeline().clearDiskCaches();
        WeLikeFileManager.clearAppCacheDir();
    }
}
