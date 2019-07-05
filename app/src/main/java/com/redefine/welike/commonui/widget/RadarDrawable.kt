package com.redefine.welike.commonui.widget

import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.animation.LinearInterpolator
import com.redefine.foundation.utils.ScreenUtils

/**
 *
 * Name: RadarDrawable
 * Author: weber
 * Email:
 * Comment: //TODO
 * Date: 2019-01-15 16:29
 *
 */

class RadarDrawable : Drawable(), Animatable {

    private val mScanningPaint: Paint = Paint()

    private val mRoundPaint = Paint()
    private val mRoundStrokePaint = Paint()
    private var mAnimator: ValueAnimator = ValueAnimator.ofInt(0, 360)

    private var mR: Float = 0F

    private val mTmpRect = RectF()

    private var isShowInner = false
    private var startColor = 0x12FFFFFF

    private var mProgressDegree: Int = 0

    init {
        mAnimator.addUpdateListener { animation ->
            mProgressDegree = animation.animatedValue as Int
            invalidateSelf()
        }

        mRoundPaint.isAntiAlias = true
        mRoundPaint.color = 0x12FFFFFF
        mRoundPaint.style = Paint.Style.FILL

        mRoundStrokePaint.isAntiAlias = true
        mRoundStrokePaint.style = Paint.Style.STROKE
        mRoundStrokePaint.color = 0x3BFFFFFF
        mRoundStrokePaint.strokeWidth = ScreenUtils.dip2Px(1F).toFloat()

        mAnimator.duration = 5000
        mAnimator.interpolator = LinearInterpolator()
        mAnimator.repeatCount = ValueAnimator.INFINITE
        mAnimator.repeatMode = ValueAnimator.RESTART

    }

    override fun draw(canvas: Canvas) {
        if (mScanningPaint.shader == null && !bounds.isEmpty) {
            mScanningPaint.shader = SweepGradient(bounds.width() * 0.5F, bounds.height() * 0.5F, Color.TRANSPARENT, startColor)
            if (isShowInner) {
                mR = Math.min(bounds.width(), bounds.height()) / 2F

                mTmpRect.set(0F, 0F, 2 * mR, 2 * mR)
            } else {
                mR = Math.sqrt((bounds.width() * bounds.width() / 4F + bounds.height() * bounds.height() / 4F).toDouble()).toFloat()
                mTmpRect.set(-mR, -mR, 2 * mR, 2 * mR)
            }
        }
        canvas.save()
        canvas.rotate(mProgressDegree.toFloat(), (bounds.width() / 2).toFloat(), (bounds.height() / 2).toFloat())
        canvas.drawOval(mTmpRect, mScanningPaint)
        canvas.restore()
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }

    fun setStartColor(startColor: Int) {
        this.startColor = startColor
        mScanningPaint.shader = null
    }

    override fun isRunning(): Boolean {
        return mAnimator.isRunning
    }

    override fun start() {
        if (!mAnimator.isRunning) {
            mAnimator.start()
        }
    }

    override fun stop() {
        if (mAnimator.isRunning) {
            mAnimator.cancel()
        }

    }

    fun setShowInnerCircle(inner: Boolean) {
        isShowInner = inner
        mScanningPaint.shader = null
    }
}