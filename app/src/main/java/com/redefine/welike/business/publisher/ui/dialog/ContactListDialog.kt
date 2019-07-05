package com.redefine.welike.business.publisher.ui.dialog

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.redefine.foundation.utils.ScreenUtils
import com.redefine.welike.R
import com.redefine.welike.business.publisher.management.PublishAnalyticsManager
import com.redefine.welike.business.publisher.ui.contract.IContactListContract
import com.redefine.welike.business.user.management.bean.User
import com.redefine.welike.commonui.event.model.PublishEventModel
import kotlinx.android.synthetic.main.activity_contact_list.*

/**
 * @author qingfei.chen
 * @date 2018/11/29
 * Copyright (C) 2018 redefine , Inc.
 */
class ContactListDialog(val context: AppCompatActivity, theme: Int, val listener: IContactListContract.OnContactChoiceListener)
    : BottomSheetDialog(context, theme), IContactListContract.OnContactChoiceListener {

    private lateinit var mPresenter: IContactListContract.IContactListPresenter
    private lateinit var mEventModel: PublishEventModel

    companion object {
        fun showDialog(context: AppCompatActivity, listener: IContactListContract.OnContactChoiceListener) {
            val bottomSheet = ContactListDialog(context, R.style.BottomSheetEdit, listener)
            val view = View.inflate(context, R.layout.activity_contact_list, null)
            bottomSheet.setContentView(view)

            val bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
            bottomSheetBehavior.peekHeight = ScreenUtils.getScreenHeight(context) - ScreenUtils.dip2Px(160f)

            bottomSheet.show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val designBottomSheet = window.findViewById<View>(R.id.design_bottom_sheet)
        designBottomSheet.setBackgroundResource(R.drawable.shape_bottom_sheet_dialog_drag_bg)
        mEventModel = PublishAnalyticsManager.getInstance().obtainCurrentModel()
        mEventModel.proxy.report6()
        mPresenter = IContactListContract.IContactListFactory.createPresenter(context, this)

        mPresenter.onCreate(findViewById(R.id.contact_list_root_view), savedInstanceState)
//        common_empty_view.showEmptyText(getContext().getString(R.string.contact_empty))
        val bottomSheetBehavior = BottomSheetBehavior.from(contact_list_root_view.parent as View)

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        drag_view.visibility = View.INVISIBLE
                        search_bar.setSearchBarShadow(true)
                        designBottomSheet.setBackgroundColor(context.resources.getColor(R.color.white))
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        drag_view.visibility = View.VISIBLE
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
        mPresenter.onDestroy()
    }

    override fun onUserChoice(user: User?) {
        user?.let {
            mEventModel.proxy.report9()

        }
        listener.onUserChoice(user)
        this.cancel()
    }
}

