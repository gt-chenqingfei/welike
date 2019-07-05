package com.redefine.welike.base.util


import android.content.Context
import android.os.Environment
import android.os.StatFs
import android.text.format.Formatter
import java.io.BufferedReader
import java.io.File
import java.io.FileReader


/**
 *
 * Name: MemoryExt
 * Author: weber
 * Email:
 * Comment: //TODO
 * Date: 2019-02-12 17:53
 *
 */

object MemoryExt {
    private const val MEM_INFO_PATH = "/proc/meminfo"
    private const val MEMTOTAL = "MemTotal"
    private const val MEMFREE = "MemFree"
    private const val Buffers = "Buffers"
    private const val Cached = "Cached"

    /**
     * 得到中内存大小
     *
     * @param context
     * @param memtotal
     * @return
     */
    fun getTotalMemory(context: Context): String? {
        return getMemInfoIype(context, MEMTOTAL)
    }

    /**
     * 得到可用内存大小
     *
     * @param context
     * @param memfree
     * @return
     */
    fun getMemoryFree(context: Context): String? {
        return getMemInfoIype(context, MEMFREE)
    }

    fun getMemoryBuffers(context: Context): String? {
        return getMemInfoIype(context, Buffers)
    }

    fun getMemoryCached(context: Context): String? {
        return getMemInfoIype(context, Cached)
    }

    /**
     * 得到type info
     *
     * @param context
     * @param type
     * @return
     */
    fun getMemInfoIype(context: Context, type: String): String? {
        try {
            val fileReader = FileReader(MEM_INFO_PATH)
            val bufferedReader = BufferedReader(fileReader)
            var line: String? = null
            while (true) {
                line = bufferedReader.readLine() ?: break
                if (line.isNotEmpty() && line.contains(type)) {
                    break
                }
            }
            bufferedReader.close()
            /* \\s表示   空格,回车,换行等空白符,
            +号表示一个或多个的意思     */
            val array = line?.split("\\s+".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
                    ?: arrayOf()
            // 获得系统总内存，单位是KB，乘以1024转换为Byte
            val length = array[1].toLong() * 1024
            return Formatter.formatFileSize(context, length)
        } catch (e: Throwable) {
        }
        return null
    }


    //ROM大小
    fun getRomTotalSpace(context: Context): String {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSize
        val totalBlocks = stat.blockCount
        return Formatter.formatFileSize(context, totalBlocks * blockSize.toLong())
    }

    fun getRomAvailSpace(context: Context): String {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSize
        val availableBlocks = stat.availableBlocks
        return Formatter.formatFileSize(context, availableBlocks * blockSize.toLong())
    }
}
