package com.redefine.welike.business.publisher.management.handler

import android.text.TextUtils
import com.redefine.welike.MyApplication
import com.redefine.welike.base.io.WeLikeFileManager
import com.redefine.welike.business.publisher.management.draft.PicItemDraft
import top.zibin.luban.Luban
import java.io.FileNotFoundException
import java.util.concurrent.CountDownLatch

/**
 *
 * Name: IPicCompressHandler
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-07-10 23:01
 *
 */

class PicCompressRunnable constructor(private val doneSignal: CountDownLatch, private val picItemDraft: PicItemDraft) : Runnable {

    override fun run() {
        if (!TextUtils.isEmpty(picItemDraft.url)) {
            doneSignal.countDown()
            return
        }

        if (picItemDraft.localFileName.contains("http://") || picItemDraft.localFileName.contains("https://")) {
            picItemDraft.url = picItemDraft.localFileName
            doneSignal.countDown()
            return
        }

        try {
            val file = Luban.with(MyApplication.getAppContext())
                    .setTargetDir(WeLikeFileManager.getTempCacheDir().absolutePath)
                    .setCompressQuality(80)
                    .ignoreBy(0).get(picItemDraft.localFileName)
            if (file.width != 0) {
                picItemDraft.width = file.width
            }
            if (file.height != 0) {
                picItemDraft.height = file.height
            }
            picItemDraft.fileSize = file.file.length()
            picItemDraft.uploadLoadFileName = file.absolutePath
            doneSignal.countDown()
        } catch (e: FileNotFoundException) {
            com.redefine.welike.business.publisher.e("Publisher", "PicCompressRunnable e:" + e.toString())
            doneSignal.countDown()
        } catch (e: NullPointerException) {
            doneSignal.countDown()
            com.redefine.welike.business.publisher.e("Publisher", "PicCompressRunnable e:" + e.toString())
        } catch (e: Exception) {
            doneSignal.countDown()
            com.redefine.welike.business.publisher.e("Publisher", "PicCompressRunnable e:" + e.toString())
        }

    }

}