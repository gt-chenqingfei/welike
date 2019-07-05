package com.redefine.welike.base.statusbar

import android.annotation.SuppressLint
import android.os.Build
import android.os.Environment
import android.support.annotation.RequiresApi
import android.view.View
import android.view.Window
import android.view.WindowManager
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*

/**
 * 状态栏亮色icon适配
 */

internal object LightStatusBarCompat {

    private val IMPL: ILightStatusBar

    internal interface ILightStatusBar {
        /**
         * Set whether ths status bar is light
         *
         * @param window         The window to set
         * @param lightStatusBar True if the status bar color is light
         */
        fun setLightStatusBar(window: Window, lightStatusBar: Boolean)
    }

    init {
        when {
            MIUILightStatusBarImpl.isMe -> IMPL = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                object : MLightStatusBarImpl() {
                    private val DELEGATE = MIUILightStatusBarImpl()

                    override fun setLightStatusBar(window: Window, lightStatusBar: Boolean) {
                        super.setLightStatusBar(window, lightStatusBar)
                        DELEGATE.setLightStatusBar(window, lightStatusBar)
                    }
                }
            } else {
                MIUILightStatusBarImpl()
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> IMPL = MLightStatusBarImpl()
            MeizuLightStatusBarImpl.isMe -> IMPL = MeizuLightStatusBarImpl()
            else -> IMPL = object : ILightStatusBar {
                override fun setLightStatusBar(window: Window, lightStatusBar: Boolean) {}
            }
        }
    }

    fun setLightStatusBar(window: Window, lightStatusBar: Boolean) {
        IMPL.setLightStatusBar(window, lightStatusBar)
    }

    private open class MLightStatusBarImpl : ILightStatusBar {

        @RequiresApi(Build.VERSION_CODES.M)
        override fun setLightStatusBar(window: Window, lightStatusBar: Boolean) {
            // 设置浅色状态栏时的界面显示
            val decor = window.decorView
            var ui = decor.systemUiVisibility
            ui = if (lightStatusBar) {
                ui or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                ui and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
            decor.systemUiVisibility = ui

        }
    }

    private class MIUILightStatusBarImpl : ILightStatusBar {

        @SuppressLint("PrivateApi")
        override fun setLightStatusBar(window: Window, lightStatusBar: Boolean) {
            val clazz = window.javaClass
            try {
                val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
                val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
                val darkModeFlag = field.getInt(layoutParams)
                val extraFlagField = clazz.getMethod("setExtraFlags", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
                extraFlagField.invoke(window, if (lightStatusBar) darkModeFlag else 0, darkModeFlag)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        companion object {

            private val KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code"
            private val KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name"
            private val KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage"

            internal// ignore all exception
            val isMe: Boolean
                get() {
                    var `is`: FileInputStream? = null
                    try {
                        `is` = FileInputStream(File(Environment.getRootDirectory(), "build.prop"))
                        val prop = Properties()
                        prop.load(`is`)
                        return (prop.getProperty(KEY_MIUI_VERSION_CODE) != null
                                || prop.getProperty(KEY_MIUI_VERSION_NAME) != null
                                || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE) != null)
                    } catch (e: IOException) {
                        return false
                    } finally {
                        if (`is` != null) {
                            try {
                                `is`.close()
                            } catch (e: IOException) {
                            }

                        }
                    }
                }
        }
    }

    private class MeizuLightStatusBarImpl : ILightStatusBar {

        override fun setLightStatusBar(window: Window, lightStatusBar: Boolean) {
            val params = window.attributes
            try {
                val darkFlag = WindowManager.LayoutParams::class.java.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
                val meizuFlags = WindowManager.LayoutParams::class.java.getDeclaredField("meizuFlags")
                darkFlag.isAccessible = true
                meizuFlags.isAccessible = true
                val bit = darkFlag.getInt(null)
                var value = meizuFlags.getInt(params)
                value = if (lightStatusBar) {
                    value or bit
                } else {
                    value and bit.inv()
                }
                meizuFlags.setInt(params, value)
                window.attributes = params
                darkFlag.isAccessible = false
                meizuFlags.isAccessible = false
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        companion object {
            internal val isMe: Boolean
                get() = Build.DISPLAY.startsWith("Flyme")
        }
    }
}
