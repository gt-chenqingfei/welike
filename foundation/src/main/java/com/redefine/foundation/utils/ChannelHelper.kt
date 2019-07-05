package com.redefine.foundation.utils

import android.app.WallpaperManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.text.TextUtils
import com.redefine.foundation.utils.walle.ChannelInfo
import com.redefine.foundation.utils.walle.ChannelReader
import java.io.File
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock



fun getChannelInfo(context: Context): ChannelInfo? {
    val apkPath = getApkPath(context)
    return if (TextUtils.isEmpty(apkPath)) {
        null
    } else ChannelReader.get(File(apkPath!!))
}

fun getApkPath(context: Context): String? {
    var apkPath: String? = null
    try {
        val applicationInfo = context.applicationInfo ?: return null
        apkPath = applicationInfo.sourceDir
    } catch (e: Throwable) {
    }
    return apkPath
}

class WalleChannelReader {

    /**
     * get channel or default
     *
     * @param context context
     * @param defaultChannel default channel
     * @return channel, default if not fount
     */
//    @JvmOverloads
//    fun getChannel(context: Context, defaultChannel: String = null): String? {
//        val channelInfo = getChannelInfo(context) ?: return defaultChannel
//        return channelInfo.channel
//    }

    /**
     * get channel info (include channle & extraInfo)
     *
     * @param context context
     * @return channel info
     */
    fun getChannelInfo(context: Context): ChannelInfo? {
        val apkPath = getApkPath(context)
        return if (TextUtils.isEmpty(apkPath)) {
            null
        } else ChannelReader.get(File(apkPath!!))
    }

    /**
     * get value by key
     *
     * @param context context
     * @param key     the key you store
     * @return value
     */
    operator fun get(context: Context, key: String): String? {
        val channelMap = getChannelInfoMap(context) ?: return null
        return channelMap[key]
    }

    /**
     * get all channl info with map
     *
     * @param context context
     * @return map
     */
    fun getChannelInfoMap(context: Context): Map<String, String>? {
        val apkPath = getApkPath(context)
        return if (TextUtils.isEmpty(apkPath)) {
            null
        } else ChannelReader.getMap(File(apkPath!!))
    }


}