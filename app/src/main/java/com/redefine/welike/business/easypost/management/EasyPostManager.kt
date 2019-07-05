package com.redefine.welike.business.easypost.management

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.text.TextUtils
import com.google.gson.Gson
import com.redefine.commonui.fresco.loader.BigPicUrlLoader
import com.redefine.im.threadTry
import com.redefine.richtext.RichContent
import com.redefine.richtext.RichItem
import com.redefine.welike.MyApplication
import com.redefine.welike.base.ErrorCode
import com.redefine.welike.base.io.WeLikeFileManager
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.business.easypost.api.PostStatusApiService
import com.redefine.welike.business.easypost.api.bean.PostStatusList
import com.redefine.welike.business.publisher.management.FeedPoster
import com.redefine.welike.business.publisher.management.PublishAnalyticsManager
import com.redefine.welike.business.publisher.management.bean.DraftPicAttachment
import com.redefine.welike.business.publisher.management.bean.DraftPost
import com.redefine.welike.kext.readAssets
import com.redefine.welike.net.RetrofitManager
import com.redefine.welike.net.subscribeExt
import io.reactivex.schedulers.Schedulers

object EasyPostManager {
    internal var sp: SharedPreferences? = null
    private var entranceAnimShown = 0 //是否显示入口动画
    private var tipShown = 0 //是否显示 引导滑动 动画，0 为未设置，1为显示，2为不显示。

    private val KEY_TIP = "KEY_TIP"

    /**
     * 是否显示 引导滑动 动画
     */
    fun checkTip(): Boolean {
        if (tipShown == 0) {
            tipShown = sp?.getInt(KEY_TIP, 1) ?: 1
        }
        return if (tipShown == 1) {
            tipShown = 2
            sp?.let { it.edit().putInt(KEY_TIP, 2).apply() }
            true
        } else {
            false
        }
    }

    fun checkShow(): Boolean {
        if (++entranceAnimShown == 1) {
            sp?.let {
                it.edit().putInt("entranceAnimShown", 10).apply()
            }
            return true
        }
        return false
    }

    fun getEasyData(): PostStatusList {
        var postList: PostStatusList? = null
        sp?.let {
            val jsonStr = it.getString("list", "")
            if (!jsonStr.isNullOrEmpty()) {
                try {
                    postList = Gson().fromJson<PostStatusList>(jsonStr, PostStatusList::class.java)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        if (postList == null || postList?.list == null) {
            postList = readFromAssets()
        } else if (postList?.list?.isEmpty()!!) {
            postList = readFromAssets()
        }

        return postList!!
    }

    private fun readFromAssets(): PostStatusList {
        val raw = MyApplication.getAppContext().readAssets("easy/default.json")
        try {
            return Gson().fromJson<PostStatusList>(raw, PostStatusList::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return PostStatusList(listOf())
    }

    fun init(context: Context) {
        threadTry {
            //取值，然后设置到内存。
            sp = context.getSharedPreferences("EasyPost", Context.MODE_PRIVATE)
            entranceAnimShown = sp?.getInt("entranceAnimShown", 0) ?: 0
            if (AccountManager.getInstance().isLogin) {
                initData(context)
            }
        }
    }

    private fun initData(context: Context) {
        val mPostStatusService = RetrofitManager.getInstance().retrofit.create(PostStatusApiService::class.java)
        mPostStatusService.getAllStatus().subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribeExt({
            if (it.code == ErrorCode.ERROR_NETWORK_SUCCESS) {
                updateLocalPreference(it.result)
            } else {
                checkLocalPreferenceIfNull(MyApplication.getAppContext())
            }
            prefeatchImageToDiskCache()
        }, {
            checkLocalPreferenceIfNull(MyApplication.getAppContext())
        })
    }

    fun send(topic: String, bitmap: Bitmap, draftId: String?) {

        threadTry {
            val w = bitmap.width
            val h = bitmap.height

            var target = WeLikeFileManager.getPhotoSaveFile("${System.currentTimeMillis()}.jpeg")
            WeLikeFileManager.saveBitmap(target, bitmap)

            var post = DraftPost()


            post.draftId = draftId
            post.setIsSaveDB(true)
            var content = RichContent()
            content.summary = topic
            content.text = topic

            post.content = content

            val picAttachment = DraftPicAttachment(target.path)
            picAttachment.mimeType = "jpeg"
            picAttachment.height = h
            picAttachment.width = w

            post.picDraftList = ArrayList<DraftPicAttachment>()
            post.picDraftList.add(picAttachment)


            val richItems = ArrayList<RichItem>().also {
                if (!TextUtils.isEmpty(topic)) {
                    it.add(RichItem().apply {
                        display = topic
                        index = 0
                        length = topic.length
                        source = topic
                        type = RichItem.RICH_TYPE_TOPIC
                    })
                }
            }

            post.content.richItemList = richItems
            var eventModel = PublishAnalyticsManager.getInstance().obtainEventModel(draftId)

            eventModel.proxy.report13()
            FeedPoster.getInstance().publish(post)

        }
//        AFGAEventManager.getInstance().sendAFEvent(TrackerConstant.EVENT_SEND_POST)
    }

    private fun prefeatchImageToDiskCache() {
        val postStatus: PostStatusList = getEasyData()
        threadTry {
            for (i in 0 until postStatus.list.size) {
                var j = 4
                postStatus.list[i].picUrlList.forEach {
                    j--
                    if (j > 0) {
                        BigPicUrlLoader.getInstance().prefeatchToDiskCache(it)
                    }
                }
            }
        }

    }

    /**
     * If network data is not null than replace data to local preference
     */
    private fun updateLocalPreference(postStatus: PostStatusList?) {
        if (postStatus == null) {
            checkLocalPreferenceIfNull(MyApplication.getAppContext())
            return
        }

        if (postStatus.list == null || postStatus.list.isEmpty()) {
            checkLocalPreferenceIfNull(MyApplication.getAppContext())
            return
        }

        val jsonStr = Gson().toJson(postStatus).toString()
        sp?.let {
            it.edit().putString("list", jsonStr).apply()
        }
    }

    /**
     * If local preference is null than replace assets to local preference
     */
    private fun checkLocalPreferenceIfNull(context: Context) {
        sp?.let {
            var jsonStr = it.getString("list", null)
            if (TextUtils.isEmpty(jsonStr)) {
                jsonStr = context.readAssets("easy/default.json")
                it.edit().putString("list", jsonStr).apply()
            }
        }
    }

}

