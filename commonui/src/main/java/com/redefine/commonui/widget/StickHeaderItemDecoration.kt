package com.redefine.commonui.widget

import android.graphics.Canvas
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.support.annotation.NonNull
import android.support.v4.view.ViewCompat
import android.view.MotionEvent
import io.reactivex.android.schedulers.AndroidSchedulers




class StickHeaderItemDecoration(private val mListener: StickyHeaderInterface) : RecyclerView.ItemDecoration() {
    private var mStickyHeaderHeight: Int = 0
    private var mHeaderPos = -1
    protected var mCurrentHeader: View? = null
    private var mTopChildPosition = RecyclerView.NO_POSITION

    fun resetHeader() {
        mTopChildPosition = RecyclerView.NO_POSITION
        mHeaderPos = -1
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val topChild = parent.getChildAt(0) ?: return

        val topChildPosition = parent.getChildAdapterPosition(topChild) //最顶部 item Position
        if (topChildPosition == RecyclerView.NO_POSITION) {
            return
        }
        // check topChildPosition changed
        if (mTopChildPosition != topChildPosition) {
            mTopChildPosition = topChildPosition

            Log.w("DDAI", "mTopChildPosition >> $topChildPosition")
            //获取 header position (mTopChildPosition 的上面 一个 header position)
            val headerPos = mListener.getHeaderPositionForItem(mTopChildPosition)
            if (headerPos == -1) {
                mTopChildPosition = -1
                return
            }

            if (mHeaderPos != headerPos) { //header 替换了，绘制新的header
                mHeaderPos = headerPos
                Log.w("DDAI", "headerPos >> $headerPos")

                if (mCurrentHeader == null) {
                    mListener.getHeaderView(mTopChildPosition, parent).let {
                        mCurrentHeader = it
                        fixLayoutSize(parent, it)
                    }
                } else {
                    mListener.bindHeaderData(mCurrentHeader!!, headerPos)
                    fixLayoutSize(parent, mCurrentHeader!!)
                }
            }
        }

        //获取 header view
        val contactPoint = mCurrentHeader!!.bottom
        val childInContact = getChildInContact(parent, contactPoint, mHeaderPos)

        if (childInContact != null && mListener.isHeader(parent.getChildAdapterPosition(childInContact))) {
            moveHeader(c, mCurrentHeader!!, childInContact)
            return
        }

        drawHeader(c, mCurrentHeader!!)
    }

//    private fun getHeaderViewForItem(headerPosition: Int, parent: RecyclerView): View {
//        Log.w("DDAI", "getHeaderViewForItem")
//        val layoutResId = mListener.getHeaderLayout(headerPosition)
//        val header = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
//        mListener.bindHeaderData(header, headerPosition)
//        return header
//    }

    fun isViewClicked(x: Float, y: Float): Boolean {
        mCurrentHeader?.let {
            val translationX = ViewCompat.getTranslationX(it)
            val translationY = ViewCompat.getTranslationY(it)
            if (x >= it.left + translationX &&
                    x <= it.right + translationX &&
                    y >= it.top + translationY &&
                    y <= it.bottom + translationY) {
                return true
            }
        }
        return false
    }

    private fun drawHeader(c: Canvas, header: View) {
        c.save()
        c.translate(0f, 0f)
        val position = IntArray(2)
        header.getLocationInWindow(position)
        Log.w("DDAI", "drawHeader = !!!top= ${position[1]}; ")
        header.draw(c)
        c.restore()
    }

    private fun moveHeader(c: Canvas, currentHeader: View, nextHeader: View) {
        c.save()
        c.translate(0f, (nextHeader.top - currentHeader.height).toFloat())
        Log.w("DDAI", "moveHeader = ${(nextHeader.top - currentHeader.height)}")
        currentHeader.draw(c)
        c.restore()
    }

    private fun getChildInContact(parent: RecyclerView, contactPoint: Int, currentHeaderPos: Int): View? {
        var childInContact: View? = null
        for (i in 0 until parent.childCount) {
            var heightTolerance = 0
            val child = parent.getChildAt(i)

            //measure height tolerance with child if child is another header
            if (currentHeaderPos != i) {
                val isChildHeader = mListener.isHeader(parent.getChildAdapterPosition(child))
                if (isChildHeader) {
                    heightTolerance = mStickyHeaderHeight - child.height
                }
            }

            //add heightTolerance if child top be in display area
            val childBottomPosition: Int
            if (child.top > 0) {
                childBottomPosition = child.bottom + heightTolerance
            } else {
                childBottomPosition = child.bottom
            }

            if (childBottomPosition > contactPoint) {
                if (child.top <= contactPoint) {
                    // This child overlaps the contactPoint
                    childInContact = child
                    break
                }
            }
        }
        return childInContact
    }


    /**
     * Properly measures and layouts the top sticky header.
     *
     * @param parent ViewGroup: RecyclerView in this case.
     */
    private fun fixLayoutSize(parent: ViewGroup, view: View) {

        // Specs for parent (RecyclerView)
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

        // Specs for children (headers)
        val childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec, parent.paddingLeft + parent.paddingRight, view.layoutParams.width)
        val childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec, parent.paddingTop + parent.paddingBottom, view.layoutParams.height)

        view.measure(childWidthSpec, childHeightSpec)

        mStickyHeaderHeight = view.measuredHeight
        view.layout(0, 0, view.measuredWidth, mStickyHeaderHeight)
    }

    interface StickyHeaderInterface {

        /**
         * This method gets called by [StickHeaderItemDecoration] to fetch the position of the header item in the adapter
         * that is used for (represents) item at specified position.
         *
         * @param itemPosition int. Adapter's position of the item for which to do the search of the position of the header item.
         * @return int. Position of the header item in the adapter.
         */
        fun getHeaderPositionForItem(itemPosition: Int): Int

        /**
         * This method gets called by [StickHeaderItemDecoration] to get layout resource id for the header item at specified adapter's position.
         *
         * @param headerPosition int. Position of the header item in the adapter.
         * @return int. Layout resource id.
         */
        fun getHeaderLayout(headerPosition: Int): Int

        fun getHeaderView(headerPosition: Int, parent: RecyclerView): View

        /**
         * This method gets called by [StickHeaderItemDecoration] to setup the header View.
         *
         * @param header         View. Header to set the data on.
         * @param headerPosition int. Position of the header item in the adapter.
         */
        fun bindHeaderData(header: View, headerPosition: Int)

        /**
         * This method gets called by [StickHeaderItemDecoration] to verify whether the item represents a header.
         *
         * @param itemPosition int.
         * @return true, if item at the specified adapter's position represents a header.
         */
        fun isHeader(itemPosition: Int): Boolean
    }

    fun setHeaderClickListener(recyclerView: RecyclerView, work: (view: View) -> Unit) {
        recyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {

            override fun onTouchEvent(rv: RecyclerView?, e: MotionEvent?) {
            }

            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (e.action == MotionEvent.ACTION_UP) {
                    // find the view on the header that was clicked
                    if (isViewClicked(e.x, e.y)) {
                        // you clicked the button.
                        mCurrentHeader?.let {
                            AndroidSchedulers.mainThread().scheduleDirect { work(it) }
                        }
                        return true
                    }
                }
                return false
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
            }
        })
    }
}