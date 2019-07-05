package com.redefine.welike.business.feeds.management

import android.app.Service
import android.media.AudioManager
import android.media.RingtoneManager
import android.media.SoundPool
import com.alibaba.fastjson.JSONObject
import com.redefine.foundation.utils.LogUtil
import com.redefine.welike.MyApplication
import com.redefine.welike.R
import com.redefine.welike.base.SpManager
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.base.request.BaseRequest
import com.redefine.welike.base.request.RequestCallback
import com.redefine.welike.business.browse.management.request.DigitaNumRequest
import com.redefine.welike.common.abtest.ABKeys
import com.redefine.welike.common.abtest.ABTest
import android.content.Context.VIBRATOR_SERVICE
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.Vibrator
import com.media.mediasdk.OpenGL.processor.FilterParams.context



/**
 * Created by nianguowang on 2019/1/17
 */
object RecommendFollowManager {


    fun updateFollowCountBase() {
        try {
            DigitaNumRequest(MyApplication.getAppContext()).req(object : RequestCallback {
                override fun onError(request: BaseRequest, errCode: Int) {
                    LogUtil.d("wng_", "DigitaNumRequest --> errCode : $errCode")
                }

                @Throws(Exception::class)
                override fun onSuccess(request: BaseRequest, result: JSONObject) {
                    var concernNum = result.getIntValue("concernNum")
                    SpManager.Config.setFollowCountBase(MyApplication.getAppContext(), concernNum)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun getFollowCountBase(): Int {
        return SpManager.Config.getFollowCountBase(MyApplication.getAppContext())
    }

    fun shouldAlert(): Boolean {
        val abt = ABTest.check(ABKeys.TEST_RECOMMEND) == 0
        if (abt) {
            return false
        }
        AccountManager.getInstance().account?.let {
            return it.followUsersCount <= getFollowCountBase()
        }
        return false
    }

    fun ring() {
        val uri = Uri.parse("android.resource://com.redefine.welike/raw/" + R.raw.follow_ring)
        val ringtone = RingtoneManager.getRingtone(MyApplication.getAppContext(), uri)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val audioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            ringtone.audioAttributes = audioAttributes
        }
        ringtone.play()
    }

    fun vibrate() {
        val vib = MyApplication.getAppContext().getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        vib.vibrate(300)
    }

    fun ringAndVibrate() {
        ring()
        vibrate()
    }
}