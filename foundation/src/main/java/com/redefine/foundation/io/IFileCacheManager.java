package com.redefine.foundation.io;

import java.io.File;
import java.util.List;

/**
 * Created by liwenbo on 2018/3/12.
 */

public interface IFileCacheManager {
    /**
     * 外置和内置 data/data/package/caches
     * @param fileName
     * @return
     */
    File getAndroidCachePath(String fileName);

    /**
     * 只是内置 data/data/package/caches
     * @param fileName
     * @return
     */
    File getAndroidCacheInnerPath(String fileName);

    /**
     * 外置和内置 data/data/package/caches
     * @param fileName
     * @return
     */
    List<File> getAndroidCacheListPath(String fileName);

    /**
     * 外置和内置 sdcard/
     * @param fileName
     * @return
     */
    File getAndroidRootFile(String fileName);


    /**
     * 外置和内置 data/data/package/files
     * @param fileName
     * @return
     */
    File getAndroidFiles(String fileName);

    /**
     * 获取可删除缓存的目录大小，注意，这里是同步获取
     * @return
     * @param excludeDir
     */
    long getCacheSize(File excludeDir);

    List<File> getCaches();
}
