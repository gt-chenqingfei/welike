package com.redefine.welike.business.manager

import android.content.Context
import android.content.SharedPreferences
import com.redefine.im.threadTry
import com.redefine.im.threadUITry
import com.redefine.im.w
import com.redefine.welike.MyApplication
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.business.im.IMHelper

object FixInfoManager {

    val sp: SharedPreferences by lazy {
        MyApplication.getApp().getSharedPreferences("FixInfoManager", Context.MODE_PRIVATE)
    }

    fun check(result: (Boolean) -> Unit) {
        threadTry {
            var key = "default"
            AccountManager.getInstance().account?.let { key = it.uid }
            val v = sp.getInt(key, 0)
            when (v) {
                0 -> sp.edit().putInt(key, 1).apply()
                1 -> sp.edit().putInt(key, 2).apply()
            }
            w("FixInfoManager check $v")
            threadUITry { result.invoke(v == 1) }
            IMHelper.getMissingStatus(listOf("a")){

            }
        }
    }
}