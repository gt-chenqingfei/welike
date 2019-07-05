package com.redefine.welike.common.util

import android.content.Context
import android.content.SharedPreferences
import com.redefine.welike.MyApplication
import java.util.*

object OnceUtil {

//    val sp: SharedPreferences by lazy { MyApplication.getApp().getSharedPreferences("OnceUtil", Context.MODE_PRIVATE) }
//
//    fun everyDay(key: String, work: () -> Unit) {
//        val startCalendar = Calendar.getInstance().also { it.time = Date(sp.getLong(key, 0)) }
//        val endCalendar = Calendar.getInstance().also { it.time = Date(System.currentTimeMillis()) }
//        val between = endCalendar.get(Calendar.DAY_OF_YEAR) - startCalendar.get(Calendar.DAY_OF_YEAR)
//        if (between >= 1) {
//            sp.edit().putLong(key, System.currentTimeMillis()).apply()
//            work()
//        }
//    }
}