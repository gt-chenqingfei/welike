package com.redefine.welike.common

import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import com.redefine.welike.base.ext.setFullScreenActivity

object WindowUtil {
    fun setFullScreen(activity: AppCompatActivity) {
        setFullScreenActivity(activity)
    }

    /**
     * 主要针对处理透明主题 输入框弹出卡顿问题 ，
     * 是因为透明主题adjust size 会把前一个页面也做重绘
     */
    fun resumeSoftInputMode(window: Window?, softInputMode: Int) {
        if (softInputMode != -1 && window != null) {
            window.setSoftInputMode(softInputMode)
        }
    }

    /**
     * 主要针对处理透明主题 输入框弹出卡顿问题 ，
     * 是因为透明主题adjust size 会把前一个页面也做重绘
     */
    fun pauseSoftInputMode(window: Window?): Int {
        var softInputMode = -1
        if (window == null) {
            return softInputMode
        }

        val attrs = window.attributes
        if (attrs != null) {
            softInputMode = attrs.softInputMode
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        }
        return softInputMode
    }
}