package com.redefine.welike.business.easypost.management

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import com.redefine.im.threadTry
import com.redefine.im.threadUITry
import com.redefine.welike.base.io.WeLikeFileManager
import com.redefine.welike.base.resource.ResourceTool
import com.redefine.welike.business.easypost.api.bean.PostStatus
import com.redefine.welike.business.easypost.api.bean.PostStatusList
import java.util.*

class EasyPostViewModel(app: Application) : AndroidViewModel(app) {
    var x = 1

    val status = MutableLiveData<Int>()
    val sending = MutableLiveData<SendingEnum>()

    lateinit var data: PostStatusList

    lateinit var currentPostCategory: PostStatus

    private var randomTextIndex = 0

    fun init() {
        threadTry {
            data = EasyPostManager.getEasyData()
            status.postValue(1)
        }
    }

    fun getRandomText(): String {
        if (currentPostCategory.contentList.isEmpty()) {
            return ""
        }

        getRandomIndex()
        return currentPostCategory.contentList[randomTextIndex]
    }

    private fun getRandomIndex(): Int {
        if (currentPostCategory.contentList.isEmpty()
                || currentPostCategory.contentList.size == 1) {
            return 0
        }
        val index = Random().nextInt(currentPostCategory.contentList.size)

        if (index == randomTextIndex) {
            return getRandomIndex()
        }
        randomTextIndex = index
        return randomTextIndex
    }

    fun getPostStatusCategorys(): List<PostStatus> {
        return data.list
    }

    fun getCurrent(): PostStatus {
        return currentPostCategory
    }

    fun setCurrent(current: PostStatus) {
        currentPostCategory = current
    }

    fun send(topic: String, bitmap: Bitmap,draftId:String?) {
        sending.postValue(SendingEnum.SENDING)
        EasyPostManager.send(topic, bitmap,draftId)
    }

    fun saveBitmap(bitmap: Bitmap, context: Context) {
        threadTry {
            var file = WeLikeFileManager.getPhotoSaveFile("${System.currentTimeMillis()}.jpeg")
            WeLikeFileManager.saveBitmap(file, bitmap)
            WeLikeFileManager.notifySystemMedia(file, context)
            threadUITry {
                Toast.makeText(context,
                        ResourceTool.getString(ResourceTool.ResourceFileEnum.PIC_SELECTOR,
                                "picture_save_success") + "\n" + file.getAbsolutePath(),
                        Toast.LENGTH_LONG).show()
            }
        }

    }
}