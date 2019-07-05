package com.redefine.welike.common.animation

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator

object Animations {
    fun getShareAnim(view: View): AnimatorSet {
        val animatorX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.3f, 1f)
        animatorX.duration = 2000
        animatorX.interpolator = DecelerateInterpolator()
        animatorX.repeatCount = ValueAnimator.INFINITE
        val animatorY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.3f, 1f)
        animatorY.duration = 2000
        animatorY.interpolator = DecelerateInterpolator()
        animatorY.repeatCount = ValueAnimator.INFINITE
        val animSet = AnimatorSet()
        animSet.play(animatorX).with(animatorY)
        return animSet
    }
}