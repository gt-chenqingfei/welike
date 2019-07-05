package com.redefine.welike.business.growth.ui

import android.graphics.PointF
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

/**
 *
 * Name: RadarLayoutManager
 * Author: weber
 * Email:
 * Comment: //TODO
 * Date: 2019-01-14 14:47
 *
 */

class RadarLayoutManager: RecyclerView.LayoutManager() {

    private val itemCoordinate: MutableList<PointF> = mutableListOf()

    companion object {
        private const val DEFAULT_MAX_COUNT: Int = 10
    }

    init {
        itemCoordinate.add(PointF(0.57F, 0F))
        itemCoordinate.add(PointF(0.31F, 0.74F))
        itemCoordinate.add(PointF(0.06F, 0.33F))
        itemCoordinate.add(PointF(0.66F, 0.22F))
        itemCoordinate.add(PointF(0.07F, 0.56F))
        itemCoordinate.add(PointF(0.76F, 0.44F))
        itemCoordinate.add(PointF(0.11F, 0.06F))
        itemCoordinate.add(PointF(0.50F, 0.56F))
        itemCoordinate.add(PointF(0.37F, 0.18F))
        itemCoordinate.add(PointF(0.71F, 0.72F))
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State?) {
        if (itemCount <= 0 && state?.isPreLayout != false) {
            return
        }

        detachAndScrapAttachedViews(recycler)
        val count = Math.min(itemCount, DEFAULT_MAX_COUNT)
        for (i in 0 until count) {
            val view = recycler.getViewForPosition(i)
            addView(view)
            measureChildWithMargins(view, 0 , 0)
            val x = width.times(itemCoordinate[i].x).toInt()
            val y = height.times(itemCoordinate[i].y).toInt()
            layoutDecoratedWithMargins(view
                    , x
                    , y
                    , x + view.measuredWidth
                    , y + view.measuredHeight)
        }
    }



}