package com.redefine.im.management

import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.content.SharedPreferences
import com.redefine.welike.base.util.LifecycleHandler

object UniqueFilter {
    lateinit var sp: SharedPreferences

    fun init(context: Context) {
        sp = context.getSharedPreferences("UniqueFilter", Context.MODE_PRIVATE)
    }

    fun check(id: String): Boolean = sp.getInt(id, 0) == 0

    fun record(id: String) = sp.edit().putInt(id, 1).apply()

    fun reset() = sp.edit().clear().apply()
}