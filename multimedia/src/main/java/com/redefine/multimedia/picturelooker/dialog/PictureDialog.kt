package com.redefine.multimedia.picturelooker.dialog

/**
 * Created by honglin on 2018/6/4.
 */
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.redefine.multimedia.R


class PictureDialog : Dialog {

    constructor(context: Context?, themeResId: Int) : super(context, themeResId)

    constructor(context: Context) : this(context, R.style.picture_alert_dialog) {
        setContentView(R.layout.picture_alert_dialog)
        setCancelable(true)
        setCanceledOnTouchOutside(false)
        val window = window
        window!!.setWindowAnimations(R.style.DialogWindowStyle)
    }

}