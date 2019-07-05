package com.redefine.welike.kext

import android.content.SharedPreferences

fun spFireOnce(sp: SharedPreferences, key: String, work: () -> Unit) {
    val flag = sp.getBoolean(key, true)
    if (flag) {
        sp.edit().putBoolean(key, false).apply()
        work()
    }
}

fun SharedPreferences.save(key: String, value: String?) = this.edit().putString(key, value?:"").apply()
