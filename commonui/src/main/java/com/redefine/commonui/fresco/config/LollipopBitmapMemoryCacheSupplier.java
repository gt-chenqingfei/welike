package com.redefine.commonui.fresco.config;

import android.app.ActivityManager;
import android.content.Context;

import com.facebook.common.internal.Supplier;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.redefine.foundation.utils.VersionUtil;

import static com.facebook.common.util.ByteConstants.MB;

/**
 * Created by liwenbo on 2018/2/23.
 */

public class LollipopBitmapMemoryCacheSupplier implements Supplier<MemoryCacheParams> {

    public static int MAX_MEMORY_CACHE_SIZE;
    public static final int DEFAULT_MAX_MEMORY_CACHE_SIZE = 16 * MB;
    public static final int DEFAULT_MAX_ENTRIES_IN_LOLLIPOP = 56;
    public static final int DEFAULT_MAX_ENTRIES_IN_ASHMEN = 256;
    public static final int DEFAULT_MAX_EVICTION_QUEUE_SIZE_IN_LOLLIPOP = 16 * MB;
    public static final int DEFAULT_MAX_EVICTION_QUEUE_ENTRIES_IN_LOLLIPOP = DEFAULT_MAX_ENTRIES_IN_LOLLIPOP / 4;

    public LollipopBitmapMemoryCacheSupplier(Context context) {
        Object o = context.getSystemService(Context.ACTIVITY_SERVICE);
        if (o instanceof ActivityManager) {
            MAX_MEMORY_CACHE_SIZE = getMaxMemoryCacheSize(((ActivityManager) o));
        } else {
            MAX_MEMORY_CACHE_SIZE = DEFAULT_MAX_MEMORY_CACHE_SIZE;
        }
    }

    private int getMaxMemoryCacheSize(ActivityManager activityManager) {
        final int maxMemory = Math.min(activityManager.getMemoryClass() * MB, Integer.MAX_VALUE);

        if (maxMemory < 32 * MB) {
            return 4 * MB;
        } else if (maxMemory < 64 * MB) {
            return 6 * MB;
        } else {
            return maxMemory / 4;
        }
    }

    @Override
    public MemoryCacheParams get() {
        MemoryCacheParams memoryCacheParams;
        if (VersionUtil.isUpperOrEqual5_0()) {
            memoryCacheParams = new MemoryCacheParams(MAX_MEMORY_CACHE_SIZE, DEFAULT_MAX_ENTRIES_IN_LOLLIPOP,
                    DEFAULT_MAX_EVICTION_QUEUE_SIZE_IN_LOLLIPOP,
                    DEFAULT_MAX_EVICTION_QUEUE_ENTRIES_IN_LOLLIPOP,
                    MAX_MEMORY_CACHE_SIZE);
        } else {
            memoryCacheParams = new MemoryCacheParams(MAX_MEMORY_CACHE_SIZE, DEFAULT_MAX_ENTRIES_IN_ASHMEN,
                    Integer.MAX_VALUE,
                    Integer.MAX_VALUE,
                    MAX_MEMORY_CACHE_SIZE);
        }
        return memoryCacheParams;

    }
}
