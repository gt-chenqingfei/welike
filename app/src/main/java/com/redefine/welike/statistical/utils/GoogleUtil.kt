package com.redefine.welike.statistical.utils

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.redefine.im.threadTry
import com.redefine.welike.MyApplication

object GoogleUtil {

    var gaid: String? = null

    fun getGAID(context: Context, callback: (String) -> Unit) {
        threadTry {
            if (gaid == null) {
                val id = AdvertisingIdClient.getAdvertisingIdInfo(context).id
                gaid = id
            }
            callback.invoke(gaid ?: "")
        }
    }

    fun forceGAID(): String {
        if (gaid.isNullOrEmpty()) {
            try {
                gaid = AdvertisingIdClient.getAdvertisingIdInfo(MyApplication.getAppContext()).id
            } catch (e: Exception) {
            }
        }
        return gaid ?: ""
    }
}