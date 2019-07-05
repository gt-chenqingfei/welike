package com.redefine.commonui.fresco.init;

import android.content.Context;
import android.graphics.Bitmap;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.cache.BitmapMemoryCacheTrimStrategy;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.redefine.commonui.fresco.config.LollipopBitmapMemoryCacheSupplier;
import com.redefine.foundation.http.HttpManager;

import java.io.File;

/**
 * Created by liwenbo on 2018/2/23.
 */

public class FrescoInitHelper {

    public static void init(Context context, File cachePath) {
        Fresco.initialize(context, initImagePipelineConfig(context, cachePath));
    }

    public static ImagePipelineConfig initImagePipelineConfig(Context context, File cachePath) {
        ImagePipelineConfig.Builder builder = OkHttpImagePipelineConfigFactory.newBuilder(context, HttpManager.getInstance().getHttpDownloadClient());
        builder.setDownsampleEnabled(true)
//                .setBitmapMemoryCacheParamsSupplier(new LollipopBitmapMemoryCacheSupplier(context))
//                .setBitmapMemoryCacheTrimStrategy(new BitmapMemoryCacheTrimStrategy())
//                .setBitmapsConfig(Bitmap.Config.ARGB_8888)
                .setMainDiskCacheConfig(getDefaultDiskCacheConfig(context, cachePath))
                .setResizeAndRotateEnabledForNetwork(true)
                .setSmallImageDiskCacheConfig(getSmallDiskCacheConfig(context, cachePath));
        return builder.build();
    }

    public static DiskCacheConfig getSmallDiskCacheConfig(Context context, File cachePath) {
        return DiskCacheConfig.newBuilder(context)
                .setBaseDirectoryPath(cachePath)
                .setBaseDirectoryName("header")
                .setMaxCacheSize(30 * ByteConstants.MB)
                .setMaxCacheSizeOnLowDiskSpace(10 * ByteConstants.MB)
                .setMaxCacheSizeOnVeryLowDiskSpace(5 * ByteConstants.MB)
                .build();
    }

    public static DiskCacheConfig getDefaultDiskCacheConfig(Context context, File cachePath) {
        return DiskCacheConfig.newBuilder(context)
                .setBaseDirectoryPath(cachePath)
                .setBaseDirectoryName("default")
                .setMaxCacheSize(120 * ByteConstants.MB)
                .setMaxCacheSizeOnLowDiskSpace(40 * ByteConstants.MB)
                .setMaxCacheSizeOnVeryLowDiskSpace(20 * ByteConstants.MB)
                .build();
    }
}
