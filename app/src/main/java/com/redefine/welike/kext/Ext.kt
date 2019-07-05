package com.redefine.welike.kext

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.redefine.welike.R
import com.redefine.welike.base.resource.ResourceTool
import java.io.File

/**
 * Got the translated String.
 */
fun String.translate(type: ResourceTool.ResourceFileEnum = ResourceTool.ResourceFileEnum.COMMON): String {
    return ResourceTool.getString(type, this)
}

fun File.createNew(): File {
    if (!exists()) {
        // 先得到文件的上级目录，并创建上级目录，在创建文件
        parentFile.mkdir()
        createNewFile()
    }
    return this
}

fun Activity.showOrHideKeyboard(view: View, show: Boolean) {
    val inputMethodManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (show) {
        Handler().postDelayed({
            inputMethodManager.showSoftInput(view, 0)
        }, 300)
    } else {
        inputMethodManager.hideSoftInputFromWindow(this.window.peekDecorView().windowToken, 0)
    }

}

@TargetApi(Build.VERSION_CODES.M)
fun AppCompatActivity.setStatusBar(color: Int = resources.getColor(R.color.color_21)) {
    Build.VERSION.SDK_INT.let {
        if (it in 21..1000) {
//            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//            window.statusBarColor = Color.TRANSPARENT
        }
    }
}