package com.redefine.welike.guide

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.tourguide_tooltip2.view.*

class NewGuide(val mToolTip: ToolTip) {
    private var mHighlightedView: View? = null
    private var mActivity: Activity? = null
    private var mToolTipViewGroup: View? = null

    fun playOn(activity: Activity, targetView: View): NewGuide {
        mHighlightedView = targetView
        setupView(activity)
        return this
    }

    fun cleanUp() {
        mToolTipViewGroup?.viewTreeObserver?.removeOnGlobalLayoutListener(layoutListener)
        (mActivity?.window?.decorView as ViewGroup).removeView(mToolTipViewGroup)
    }

    private fun setupView(activity: Activity) {
        // TourGuide can only be setup after all the views is ready and obtain it's position/measurement
        // so when this is the 1st time TourGuide is being added,
        // else block will be executed, and ViewTreeObserver will make TourGuide setup process to be delayed until everything is ready
        // when this is run the 2nd or more times, if block will be executed
        mHighlightedView?.let {
            if (ViewCompat.isAttachedToWindow(it)) {
                setupToolTip(activity)
            } else {
                it.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        //TODO delay remove.
                        it.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        setupToolTip(activity)
                    }
                })
            }
        }
    }

    private fun setupToolTip(activity: Activity) {
        val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        mActivity = activity
        mToolTip.let {
            /* inflate and get views */
            val parent = activity.window?.decorView as ViewGroup
            val layoutInflater = activity.layoutInflater
            mToolTipViewGroup = layoutInflater.inflate(R.layout.tourguide_tooltip2, null)
            mToolTipViewGroup?.let { viewgroup ->
                //                val toolTipContainer = viewgroup.toolTip_container
                val toolTipTitleTV = viewgroup.title as ATextView
                /* set tooltip attributes */
//                toolTipContainer.setBackgroundColor(it.mBackgroundColor)
                toolTipTitleTV.setTextColor(it.mTextColor)
//                toolTipDescriptionTV.setTextColor(it.mTextColor)


                if (it.mTitle.isNullOrEmpty()) {
                    toolTipTitleTV.visibility = View.GONE
                } else {
                    toolTipTitleTV.visibility = View.VISIBLE
                    toolTipTitleTV.text = it.mTitle
                }

//                if (it.mDescription.isNullOrEmpty()) {
//                    toolTipDescriptionTV.visibility = View.GONE
//                } else {
//                    toolTipDescriptionTV.visibility = View.VISIBLE
//                    toolTipDescriptionTV.text = it.mDescription
//                }

                if (it.mWidth != -1) {
                    layoutParams.width = it.mWidth
                }
                /* add setShadow if it's turned on */
//                if (it.mShadow) {
//                    viewgroup.setBackgroundDrawable(activity.resources.getDrawable(R.drawable.tourguide_drop_shadow))
//                }

                /* position and size calculation */
                val pos = IntArray(2)
                mHighlightedView?.getLocationOnScreen(pos)
                val targetViewX = pos[0]
                val targetViewY = pos[1]

                // get measured size of tooltip
                viewgroup.measure(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
                var toolTipMeasuredWidth = if (it.mWidth != -1) it.mWidth else viewgroup.measuredWidth
                val toolTipMeasuredHeight = viewgroup.measuredHeight

                val resultPoint = Point() // this holds the final position of tooltip
                val density = activity.resources.displayMetrics.density
                val adjustment = 10 * density //adjustment is that little overlapping area of tooltip and targeted button

                // calculate x position, based on gravity, tooltipMeasuredWidth, parent max width, x position of target view, adjustment
                if (toolTipMeasuredWidth > parent.width) {
                    resultPoint.x = getXForTooTip(mHighlightedView!!, it.mGravity, parent.width, targetViewX, adjustment)
                } else {
                    resultPoint.x = getXForTooTip(mHighlightedView!!, it.mGravity, toolTipMeasuredWidth, targetViewX, adjustment)
                }

                resultPoint.y = getYForTooTip(mHighlightedView!!, it.mGravity, toolTipMeasuredHeight, targetViewY, adjustment)

                // add view to parent
                //            ((ViewGroup) mActivity.getWindow().getDecorView().findViewById(android.R.id.content)).addView(mToolTipViewGroup, layoutParams);
                parent.addView(viewgroup, layoutParams)

                // 1. width < screen check
                if (toolTipMeasuredWidth > parent.width) {
                    viewgroup.layoutParams.width = parent.width
                    toolTipMeasuredWidth = parent.width
                }
                // 2. x left boundary check
                if (resultPoint.x < 0) {
                    viewgroup.layoutParams.width = toolTipMeasuredWidth + resultPoint.x //since point.x is negative, use plus
                    resultPoint.x = 0
                }
                // 3. x right boundary check
                val tempRightX = resultPoint.x + toolTipMeasuredWidth
                if (tempRightX > parent.width) {
                    viewgroup.layoutParams.width = parent.width - resultPoint.x //since point.x is negative, use plus
                }

                // pass toolTip onClickListener into toolTipViewGroup
                if (it.mOnClickListener != null) {
                    viewgroup.setOnClickListener(it.mOnClickListener)
                }
                //arrow
                var arrowPadding = 0
                mHighlightedView?.let {
                    arrowPadding = targetViewX - resultPoint.x + (it.right - it.left) / 2
                }
                if (it.mGravity and Gravity.TOP == Gravity.TOP) {
                    toolTipTitleTV.setArrow(arrowPadding, false)
                } else if (it.mGravity and Gravity.BOTTOM == Gravity.BOTTOM) {
                    toolTipTitleTV.setArrow(arrowPadding, true)
                }
                // TODO: no boundary check for height yet, this is a unlikely case though
                // height boundary can be fixed by user changing the gravity to the other size, since there are plenty of space vertically compared to horizontally

                // this needs an viewTreeObserver, that's because TextView measurement of it's vertical height is not accurate (didn't take into account of multiple lines yet) before it's rendered
                // re-calculate height again once it's rendered
                viewgroup.viewTreeObserver.addOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener {
                    val toolTipHeightAfterLayout = viewgroup.height
                    val pos2 = IntArray(2)
                    mHighlightedView!!.getLocationOnScreen(pos2)
                    val targetViewY2 = pos2[1]
                    val fixedY = getYForTooTip(mHighlightedView!!, it.mGravity, toolTipHeightAfterLayout, targetViewY2, adjustment)
                    if (currentPos[0] != viewgroup.x.toInt() || currentPos[1] != fixedY) {
                        currentPos[0] = viewgroup.x.toInt()
                        currentPos[1] = fixedY
                        layoutParams.setMargins(viewgroup.x.toInt(), fixedY, 0, 0)
                        viewgroup.requestLayout()
                    }
                }.also { layoutListener = it })
                // set the position using setMargins on the left and top
                layoutParams.setMargins(resultPoint.x, resultPoint.y, 0, 0)
            }
        }
    }

    private var currentPos = intArrayOf(0, 0)

    private var layoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null

    private fun getXForTooTip(target: View, gravity: Int, toolTipMeasuredWidth: Int, targetViewX: Int, adjustment: Float): Int {
        return when {
            gravity and Gravity.LEFT == Gravity.LEFT -> targetViewX - toolTipMeasuredWidth + adjustment.toInt()
            gravity and Gravity.RIGHT == Gravity.RIGHT -> targetViewX + target.width - adjustment.toInt()
            else -> targetViewX + target.width / 2 - toolTipMeasuredWidth / 2
        }
    }

    private fun getYForTooTip(target: View, gravity: Int, toolTipMeasuredHeight: Int, targetViewY: Int, adjustment: Float): Int {
        return if (gravity and Gravity.TOP == Gravity.TOP) {

            if (gravity and Gravity.LEFT == Gravity.LEFT || gravity and Gravity.RIGHT == Gravity.RIGHT) {
                targetViewY - toolTipMeasuredHeight + adjustment.toInt()
            } else {
                targetViewY - toolTipMeasuredHeight - adjustment.toInt()
            }
        } else { // this is center
            if (gravity and Gravity.LEFT == Gravity.LEFT || gravity and Gravity.RIGHT == Gravity.RIGHT) {
                targetViewY + target.height - adjustment.toInt()
            } else {
                targetViewY + target.height + adjustment.toInt()
            }
        }
    }
}

