package com.redefine.welike.business.publisher.ui.dialog

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import com.redefine.foundation.utils.ScreenUtils
import com.redefine.welike.R
import com.redefine.welike.base.GlobalConfig
import com.redefine.welike.business.publisher.management.PublishAnalyticsManager
import com.redefine.welike.business.publisher.management.PublisherEventManager
import com.redefine.welike.statistical.EventLog
import kotlinx.android.synthetic.main.add_link_dialog.*

/**
 * @author qingfei.chen
 * @date 2018/12/3
 * Copyright (C) 2018 redefine , Inc.
 */
class AddLinkDialog(val context: AppCompatActivity, val listener: OnLinkSubmitListener)
    : BottomSheetDialog(context, R.style.BottomSheetEdit),
        TextWatcher {


    companion object {
        fun showDialog(context: AppCompatActivity, listener: OnLinkSubmitListener) {
            val bottomSheet = AddLinkDialog(context, listener)
            val view = View.inflate(context, R.layout.add_link_dialog, null)
            bottomSheet.setContentView(view)

            val bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
            bottomSheetBehavior.peekHeight = ScreenUtils.dip2Px(206f)
            bottomSheet.show()

            EventLog.Publish.report11(PublisherEventManager.INSTANCE.source,
                    PublisherEventManager.INSTANCE.main_source,
                    PublisherEventManager.INSTANCE.page_type,
                    PublisherEventManager.INSTANCE.at_source)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.findViewById<View>(R.id.design_bottom_sheet).setBackgroundResource(R.drawable.shape_bottom_sheet_dialog_drag_bg)
        add_link_edit_text.addTextChangedListener(this)
        add_link_submit.setOnClickListener {
            listener.onLinkAdd(add_link_edit_text.text.toString())
            PublishAnalyticsManager.getInstance().obtainCurrentModel().proxy.report12()
            cancel()
        }
        add_link_clear.setOnClickListener {
            add_link_edit_text.setText("")
        }
        PublishAnalyticsManager.getInstance().obtainCurrentModel().proxy.report11()
    }


    override fun afterTextChanged(s: Editable?) {
        val url = s.toString()
        add_link_edit_til.isHintEnabled = !TextUtils.isEmpty(url)


        if (TextUtils.isEmpty(s)) {
            add_link_edit_til.error = ""
            add_link_submit.isEnabled = false
            add_link_clear.visibility = View.GONE
            return
        }
        add_link_clear.visibility = View.VISIBLE
        if (url.toLowerCase().startsWith(GlobalConfig.HTTP)
                || url.toLowerCase().startsWith(GlobalConfig.HTTPS)) {
            add_link_edit_til.error = ""
            add_link_submit.isEnabled = true
        } else {
            add_link_edit_til.error = getContext().getString(R.string.add_link_error)
            add_link_submit.isEnabled = false
        }

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

}

interface OnLinkSubmitListener {
    fun onLinkAdd(link: String)
}