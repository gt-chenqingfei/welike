package com.redefine.multimedia.picturelooker.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity


/**
 * Created by honglin on 2018/6/4.
 */
class CustomDialog : Dialog {
    constructor(context: Context?, themeResId: Int) : super(context, themeResId)

    constructor(context: Context, width: Int, height: Int, layout: Int,
                style: Int) : this(context, style) {
        setContentView(layout)
        val window = window
        val params = window!!.attributes
        params.width = width
        params.height = height
        params.gravity = Gravity.CENTER
        window.attributes = params
    }
}