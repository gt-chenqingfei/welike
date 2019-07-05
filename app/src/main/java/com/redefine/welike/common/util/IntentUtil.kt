package com.redefine.welike.common.util

import android.app.Activity
import android.content.Intent
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.redefine.foundation.utils.ChannelHelper
import com.redefine.im.w
import com.redefine.welike.statistical.EventLog

class IntentUtil {
    fun showIntent(from: String, intent: Intent?) {
        var xx = ""
        intent?.extras?.let { ex ->
            ex.keySet()?.forEach {
                xx += "<$it :${ex[it]}>"
            }
        }
    }

    fun checkPush(intent: Intent?): String {
        return intent?.extras?.getString("clickAction", "") ?: ""
    }

    fun checkDynamicLink(activity: Activity) {
        activity.intent?.let {
            FirebaseDynamicLinks.getInstance()
                    .getDynamicLink(it)
                    .addOnSuccessListener(activity) { pendingDynamicLinkData ->
                        // Get deep link from result (may be null if no link is found)
                        val deepLink = pendingDynamicLinkData?.link
                        val tag = deepLink?.getQueryParameter("tag")
                        w("deepLink = $deepLink")
                        EventLog.UnLogin.report21(deepLink.toString())
                        ChannelHelper.saveDynamicLinkTag(tag, activity.application)
                        ChannelHelper.updateTag(tag, null)
                    }
                    .addOnFailureListener(activity) { e -> w("getDynamicLink:onFailure") }
        }
    }
}