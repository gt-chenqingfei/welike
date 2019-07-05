package com.redefine.welike.business.feeds.discovery.ui

import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.redefine.welike.R
import kotlinx.android.synthetic.main.discover_dialog_guide.*

class DiscoverGuideDialog : Dialog {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, themeResId: Int) : super(context, themeResId)
    constructor(context: Context?, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener?) : super(context, cancelable, cancelListener)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.discover_dialog_guide)
        setCanceledOnTouchOutside(true)
        setCancelable(true)

        val window = this.window
        if (window != null) {
            window.setGravity(Gravity.CENTER)
            window.setBackgroundDrawableResource(R.color.transparent)
            val lp = window.attributes
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            window.attributes = lp
//            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING or WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        }
        discover_guide_anim.repeatCount = -1
        discover_guide_anim.setAnimation("discover_guide.json")
        discover_guide_anim.playAnimation()
        //set view
        discover_guide_close.setOnClickListener { dismiss() }
        discover_guide_confirm.setOnClickListener { dismiss() }

    }
}