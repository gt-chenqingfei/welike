package com.redefine.welike.base.ext

import android.support.v7.app.AppCompatActivity
import android.view.Window
import com.redefine.welike.base.statusbar.StatusBarUtil

fun setFullScreenActivity(activity: AppCompatActivity) {
    StatusBarUtil.setFullScreenActivity(activity.window)
}

fun setFullScreenActivity(window: Window) {
    StatusBarUtil.setFullScreenActivity(window)
}

fun setStatusBarJ(activity: AppCompatActivity) {
    StatusBarUtil.initStatusBar(activity.window, true)
}

fun setStatusBarJ(window: Window) {
    StatusBarUtil.initStatusBar(window, true)
}