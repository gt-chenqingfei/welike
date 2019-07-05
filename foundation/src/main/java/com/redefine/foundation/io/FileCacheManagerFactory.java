package com.redefine.foundation.io;

import android.content.Context;

/**
 * Created by liwenbo on 2018/3/12.
 */

public class FileCacheManagerFactory {

    public static IFileCacheManager getDefaultCacheManager(Context context) {
        return new FileCacheManager(context);
    }

}
