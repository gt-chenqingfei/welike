package com.redefine.multimedia.picturelooker.widget

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.redefine.multimedia.picturelooker.adapter.SimpleFragmentAdapter
import com.redefine.multimedia.photoselector.entity.Item
import com.redefine.multimedia.picturelooker.listener.OnCallBackActivity
import com.redefine.multimedia.picturelooker.listener.OnImageLongClickListener
import com.redefine.multimedia.picturelooker.listener.OnImageLookedChangedListener
import org.jetbrains.annotations.NotNull

/**
 * Created by honglin on 2018/6/2.
 */
class PicturePreView : RelativeLayout {

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initViews()
        setViewEvent()
    }

    var onchangedListener: OnImageLookedChangedListener? = null


    private var previewViewPager: PreviewViewPager? = null

    private var adapter: SimpleFragmentAdapter? = null

    private var mItems = ArrayList<Item>()

    private var mListener: OnImageLongClickListener? = null

    fun setOnLongClickListener(listener: OnImageLongClickListener) {
        mListener = listener
        adapter?.setLongClickListener(mListener)
//        when (adapter != null) {
//            true -> adapter?.setLongClickListener(mListener)
//        }
    }

    /**
     * @param items bind data
     * @param position
     * @param activity
     * */
    fun bindData(items: ArrayList<Item>, position: Int, activity: OnCallBackActivity) {
        if (items.isEmpty()) {
            return
        }
        mItems = items
        adapter = SimpleFragmentAdapter(items, context, activity)
        previewViewPager?.adapter = adapter
        previewViewPager?.currentItem = position
        onchangedListener?.onLookedChanged(position, mItems[position])
        mListener?.let { adapter?.setLongClickListener(it) }
    }

    private fun initViews() {
        PreviewViewPager(context).also {
            it.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            previewViewPager = it
            addView(it)
        }
//        previewViewPager = PreviewViewPager(context)
//
//        previewViewPager?.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//
//        addView(previewViewPager)
    }


    private fun setViewEvent() {

        previewViewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(i: Int) {
                onchangedListener?.onLookedChanged(i, mItems[i])
//                when (onchangedListener == null) {
//                    true -> return
//                    false -> onchangedListener?.onLookedChanged(i, mItems.get(i))
//                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

}