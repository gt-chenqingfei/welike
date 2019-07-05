package com.redefine.welike.base.statusbar

import android.graphics.Color
import android.os.Build
import android.support.v4.view.ViewCompat
import android.view.View
import android.view.Window
import android.view.WindowManager

/**
 *
 * Name: StatusBarUtil
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-10-06 22:29
 *
 */

object StatusBarUtil {

    private const val TRANSPARENT_40: Int = 0x66000000

    fun initStatusBar(window: Window, isLightStatusBar: Boolean) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        } else {
            // Enables regular immersive mode.
            // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
            // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                    // Set the content to appear under the system bars so that the
                    // content doesn't resize when the system bars hide and show.
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.statusBarColor = Color.TRANSPARENT
            } else {
                window.statusBarColor = TRANSPARENT_40
            }
        }

        changeToLightStatusBar(window, isLightStatusBar)
        ViewCompat.requestApplyInsets(window.decorView)
    }

    fun setFullScreenActivity(window: Window) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return
        }
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    fun changeToLightStatusBar(window: Window, isLightStatusBar : Boolean) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        LightStatusBarCompat.setLightStatusBar(window, isLightStatusBar)
    }
}