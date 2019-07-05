package com.redefine.welike.push

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.alibaba.fastjson.JSONObject
import com.google.firebase.messaging.FirebaseMessaging
import com.redefine.foundation.language.LocalizationManager
import com.redefine.foundation.utils.ChannelHelper
import com.redefine.foundation.utils.CommonHelper
import com.redefine.im.d
import com.redefine.im.threadTry
import com.redefine.im.w
import com.redefine.welike.BuildConfig
import com.redefine.welike.MyApplication
import com.redefine.welike.base.LanguageSupportManager
import com.redefine.welike.base.SpManager
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.base.request.BaseRequest
import com.redefine.welike.base.request.RequestCallback
import com.redefine.welike.common.Life
import com.redefine.welike.common.util.OnceUtil
import com.redefine.welike.kext.save
import com.redefine.welike.statistical.utils.GoogleUtil
import kotlin.concurrent.thread

object PushService {


    val DEFAULT_TOPIC = if (BuildConfig.DEBUG) "PRE" else "ALL"

    val sp: SharedPreferences by lazy { MyApplication.getApp().getSharedPreferences("PushService", Context.MODE_PRIVATE) }
    val spCheck: SharedPreferences by lazy { MyApplication.getApp().getSharedPreferences("Push_Check", Context.MODE_PRIVATE) }

    fun subLanguageTopic() {
        threadTry {
            val fm = FirebaseMessaging.getInstance()
            val xx = LocalizationManager.all.toCollection(ArrayList<String>())
            LanguageSupportManager.getInstance().currentMenuLanguageType.let {
                if (!it.isNullOrEmpty()) {
                    fm.subscribeToTopic(it)
                    Log.d("DDAI","fm.subscribe $it")
                    xx.remove(it)
                }
            }
            xx.forEach {
                fm.unsubscribeFromTopic(it)
                Log.d("DDAI","fm.unsubscribe $it")
            }
        }
    }

    fun subTopic() {
        val tags = ArrayList<String>()
        val channel = ChannelHelper.getTags(MyApplication.getApp()).firstOrNull() ?: ""
        tags.add(channel)
        val fm = FirebaseMessaging.getInstance()
        fm.subscribeToTopic(DEFAULT_TOPIC)
        CommonHelper.getAppVersionName(MyApplication.getAppContext()).let {
            if (!it.isNullOrEmpty()) {
                fm.subscribeToTopic(it)
            }
        }
        CommonHelper.getChannel(MyApplication.getAppContext()).let {
            if (!it.isNullOrEmpty()) {
                fm.subscribeToTopic(it)
                w("FirebaseMessaging.subscribeToTopic$it")
            }
        }
        val xx = LocalizationManager.all.toCollection(ArrayList<String>())
        LanguageSupportManager.getInstance().currentMenuLanguageType.let {
            if (!it.isNullOrEmpty()) {
                fm.subscribeToTopic(it)
                xx.remove(it)
            }
        }
//        fm.subscribeToTopic(LocalizationManager.LANGUAGE_TYPE_ENG)
        xx.forEach {
            fm.unsubscribeFromTopic(it)
        }
        tags.forEach {
            fm.subscribeToTopic("${DEFAULT_TOPIC}_TAG_$it")
        }
    }


    fun saveToken(token: String) {
        sp.save(SpManager.pushNotificationTokenKeyName, token)
    }

    fun checkToken(end: () -> Unit) {
        thread {
            try {
                val token = sp.getString(SpManager.pushNotificationTokenKeyName, "")
                if (!token.isNullOrEmpty()) {
                    if (AccountManager.getInstance().isLogin) {
                        checkUpdateToken(token)
                    } else {
                        checkAddToken(token)
                    }
                }
//                val token = sp.getString(SpManager.pushNotificationTokenKeyName, "")
//                if (!token.isNullOrEmpty()) {
//                    uploadToken(token)
//                }
                subTopic()
            } catch (e: Exception) {
            } finally {
                end()
            }
        }
    }

    fun init() {
        Life.regListener {
            if (it == Life.LIFE_LOGIN) {
                checkToken {}
            } else if (it == Life.LIFE_LOGOUT) {
                spCheck.edit().clear().apply()
            }
        }
        checkToken {}
    }

    private fun checkUpdateToken(token: String) {
        val key = "UpdateToken$token"
        val time = spCheck.getInt(key, 0)
        if (time < 2) {
            try {
                TokenAddRequest(token, MyApplication.getAppContext()).req(object : RequestCallback {
                    override fun onError(request: BaseRequest?, errCode: Int) {
                    }

                    override fun onSuccess(request: BaseRequest?, result: JSONObject?) {
                        spCheck.edit().putInt(key, time + 1).apply()
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun checkAddToken(token: String) {
        val language = LocalizationManager.getInstance().currentLanguage
        val channel = ChannelHelper.getTags(MyApplication.getApp()).firstOrNull() ?: ""
        GoogleUtil.getGAID(MyApplication.getAppContext()) { gaid ->

            val key = "AddToken$token$language$channel$gaid"
            val time = spCheck.getInt(key, 0)
            if (time < 2) {
                try {
                    UploadTokenRequest(token, gaid, language, MyApplication.getAppContext(), channel).req(object : RequestCallback {
                        override fun onError(request: BaseRequest?, errCode: Int) {
                        }

                        override fun onSuccess(request: BaseRequest?, result: JSONObject?) {
                            spCheck.edit().putInt(key, time + 1).apply()
                        }
                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * 上传 token，根据登录状态不同，调用API不同
     */
//    fun uploadToken(token: String) {
//        val account = AccountManager.getInstance().account
//        val language = LocalizationManager.getInstance().currentLanguage
//        val channel = ChannelHelper.getTags(MyApplication.getApp()).firstOrNull() ?: ""
//        if (account == null || !account.isLogin) {
//            GoogleUtil.getGAID(MyApplication.getAppContext()) { gaid ->
//                OnceUtil.everyDay("UploadTokenRequest") {
//                    try {
//                        UploadTokenRequest(token, gaid, language, MyApplication.getAppContext(), channel).req(null)
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
//
//            }
//        } else {
//            try {
//                TokenAddRequest(token, MyApplication.getAppContext()).req(null)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//        }
//
//    }
}