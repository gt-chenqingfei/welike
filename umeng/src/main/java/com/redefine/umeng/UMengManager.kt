package com.redefine.umeng

import android.app.Activity
import android.app.Application
import android.os.Bundle
import cn.help.acs.ACS
import java.util.*

object UMengManager {
    /**
     * Key名称	Value说明	必选
    fr	平台，例如android	Y
    ver	版本，例如 11.0.3.11	Y
    bid	渠道标识之一	Y
    pfid		Y
    bseq	打包流水号	N
    ch	渠道标识之一	Y
    prd	产品名称	Y
    lang	语言	N
    btype	渠道类型	N
    bmode	渠道模式	N
    sver	子版本号	N
     */
    fun initUMeng(app: Application, ver: String, ch: String) {
        ACS.init(app, Bundle().apply {
            putString("fr", "android")
            putString("ver", ver)
            putString("ch", ch)
            putString("lang", Locale.getDefault().language)
            putString("prd", "welike")
        })
        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {

            override fun onActivityResumed(activity: Activity?) = ACS.onResume()

            override fun onActivityStopped(activity: Activity?) = ACS.onPause()

            override fun onActivityPaused(activity: Activity?) {}

            override fun onActivityStarted(activity: Activity?) {}

            override fun onActivityDestroyed(activity: Activity?) {}

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {}

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {}
        })
    }

    fun destory() {
        ACS.onDestory()
    }
}
