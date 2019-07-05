package com.redefine.welike.business.publisher.ui.dialog

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.view.View
import com.redefine.foundation.utils.ScreenUtils
import com.redefine.welike.R
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean
import com.redefine.welike.business.publisher.ui.contract.ITopicChoiceContract
import kotlinx.android.synthetic.main.topic_choice_layout.*


/**
 * @author qingfei.chen
 * @date 2018/11/28
 * Copyright (C) 2018 redefine , Inc.
 */
class TopicChoiceDialog(context: Context, val isBrowse: Boolean, val listener: OnTopicChoiceListener)
    : BottomSheetDialog(context, R.style.BottomSheetEdit), OnTopicChoiceListener {

    override fun onTopicChoice(topicbean: TopicSearchSugBean.TopicBean?, isWithFlag: Boolean) {
        listener.onTopicChoice(topicbean, isWithFlag)
        this.cancel()
    }

    companion object {
        fun showDialog(context: Context, isBrowse: Boolean, listener: OnTopicChoiceListener) {
            val bottomSheet = TopicChoiceDialog(context, isBrowse, listener)
            val view = View.inflate(context, R.layout.topic_choice_layout, null)
            bottomSheet.setContentView(view)

            val bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
            bottomSheetBehavior.peekHeight = ScreenUtils.getScreenHeight(context) - ScreenUtils.dip2Px(160f)
            bottomSheet.show()
        }
    }

    private var mPresenter: ITopicChoiceContract.ITopicChoicePresenter? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val designBottomSheet = window.findViewById<View>(R.id.design_bottom_sheet)
        designBottomSheet.setBackgroundResource(R.drawable.shape_bottom_sheet_dialog_drag_bg)
//        PublishAnalyticsManager.getInstance().obtainCurrentModel().proxy.report18()
        mPresenter = ITopicChoiceContract.TopicChoiceFactory.createPresenter(ownerActivity, this)
        mPresenter?.onCreate(findViewById(R.id.topic_choice_root_view), null)

        bottomSheetBehavior = BottomSheetBehavior.from(topic_choice_root_view.parent as View)
        topic_choice_root_view.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            topic_choice_root_view.getWindowVisibleDisplayFrame(rect)
            val height = topic_choice_root_view.rootView.height
            val heightDefere = height - rect.bottom
            if (heightDefere > 200) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        drag_view.visibility = View.INVISIBLE
                        search_bar.setSearchDrawable(R.drawable.ic_common_search)
                        search_bar.setSearchBarShadow(true)
                        designBottomSheet.setBackgroundColor(context.resources.getColor(R.color.white))
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        drag_view.visibility = View.VISIBLE
                        search_bar.setSearchDrawable(R.drawable.ic_publish_topic)
                        search_bar.setSearchBarShadow(false)
                        designBottomSheet.setBackgroundResource(R.drawable.shape_bottom_sheet_dialog_drag_bg)
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> cancel()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mPresenter?.onDestroy()
    }
}

interface OnTopicChoiceListener {
    fun onTopicChoice(topicbean: TopicSearchSugBean.TopicBean?, isWithFlag: Boolean)
}