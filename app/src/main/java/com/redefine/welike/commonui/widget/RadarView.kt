package com.redefine.welike.commonui.widget

import android.content.Context
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View

/**
 *
 * Name: RadarView
 * Author: weber
 * Email:
 * Comment: //TODO
 * Date: 2019-01-15 16:12
 *
 */

class RadarView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var bgDrawable: RadarDrawable = RadarDrawable()

    init {
        ViewCompat.setBackground(this, bgDrawable)
    }

    fun setShowInnerCricle(isInner: Boolean) {
        bgDrawable.setShowInnerCircle(isInner)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        bgDrawable.start()
    }

    override fun onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach()
        bgDrawable.start()
    }

    override fun onDetachedFromWindow() {
        bgDrawable.stop()
        super.onDetachedFromWindow()
    }

    override fun onStartTemporaryDetach() {
        bgDrawable.stop()
        super.onStartTemporaryDetach()
    }

    fun setStartColor(startColor: Int) {
        bgDrawable.setStartColor(startColor)
    }
}