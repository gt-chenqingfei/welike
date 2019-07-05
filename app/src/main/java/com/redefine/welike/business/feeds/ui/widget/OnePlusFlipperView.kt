package com.redefine.welike.business.feeds.ui.widget

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.ViewFlipper
import com.redefine.welike.R
import com.scwang.smartrefresh.layout.util.DensityUtil

/**
 * Created by nianguowang on 2019/1/15
 */
class OnePlusFlipperView : FrameLayout {

    private lateinit var viewFlipper: ViewFlipper
    private val mRunnable = AnimateRunnable()
    private var animateRemain = 0

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {

        viewFlipper = ViewFlipper(context)
        viewFlipper.isAutoStart = false
        viewFlipper.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        val enterAnim = AnimationUtils.loadAnimation(context, R.anim.anim_bottom_in)
        viewFlipper.inAnimation = enterAnim
        val outAnim = AnimationUtils.loadAnimation(context, R.anim.anim_top_out)
        viewFlipper.outAnimation = outAnim
        reset()

        addView(viewFlipper)
    }

    fun startFlipper() {
        animateRemain++
        removeCallbacks(mRunnable)
        if (viewFlipper.childCount > 0) {
            viewFlipper.addView(newTextView("+1"), viewFlipper.childCount - 1)
        }
        post(mRunnable)
    }

    private fun reset() {
        animateRemain = 0
        viewFlipper.removeAllViews()
        viewFlipper.addView(newTextView(" "))
        viewFlipper.addView(newTextView(" "))
    }

    private fun newTextView(message: String): TextView {
        val text = TextView(context)
        text.setTextColor(context.resources.getColor(R.color.color_ff6a49))
        text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14F)
        text.gravity = Gravity.CENTER
        text.typeface = Typeface.DEFAULT_BOLD
        text.text = message
        return text
    }

    private inner class AnimateRunnable: Runnable {
        override fun run() {
            viewFlipper.showNext()
            animateRemain--
            if (animateRemain >= 0) {
                postDelayed(this, 3000)
            } else {
                reset()
            }
        }

    }

}