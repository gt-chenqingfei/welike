package com.redefine.welike.business.publisher.ui.dialog

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatCheckBox
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import cn.dreamtobe.kpswitch.util.KeyboardUtil
import com.redefine.foundation.utils.ScreenUtils
import com.redefine.richtext.RichContent
import com.redefine.welike.R
import com.redefine.welike.base.GlobalConfig
import com.redefine.welike.business.feeds.management.bean.Comment
import com.redefine.welike.business.publisher.management.bean.CheckState
import com.redefine.welike.business.publisher.management.bean.DraftCategory
import com.redefine.welike.business.publisher.management.bean.DraftReply
import com.redefine.welike.business.publisher.viewmodel.PublishReplyViewModel
import kotlinx.android.synthetic.main.activity_publish_comment_popup.*
import kotlinx.android.synthetic.main.layout_publish_comment_check_and_limit_warn_popup.*
import kotlinx.android.synthetic.main.layout_publish_post_editor_input.*
import kotlinx.android.synthetic.main.layout_publish_tools_and_emotion_popup.*

/**
 * @author qingfei.chen
 * @date ${DATE}
 * Copyright (C) 2018 redefine , Inc.
 */
//@LayoutResource(layout = R.layout.activity_publish_reply_popup, title = R.string.editor_comment_title)
class PublishReplyPopUpDialog(context: AppCompatActivity, draftId:String, val comment:Comment) : AbsPublishDialog<DraftReply, PublishReplyViewModel>(context,draftId) {
    lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    override fun getLimitWarnView(): TextView {
        return editor_post_text_limit_warn
    }

    override fun getCheckBoxView(): AppCompatCheckBox {
        return editor_check_btn
    }

    override fun getViewModel(): PublishReplyViewModel {
        val viewModel =
                ViewModelProviders.of(context).get(PublishReplyViewModel::class.java)
        viewModel.init(DraftCategory.REPLY, draftId)
        return viewModel
    }

    override fun getRichContent(): RichContent {
        return et_publish_editor.richProcessor
                .getRichContent(true, -1, GlobalConfig.PUBLISH_COMMENT_INPUT_TEXT_MAX_OVER_LIMIT)
    }

    override fun getEventLabel(): String {
        return "comment"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bottomSheetBehavior = BottomSheetBehavior.from(editor_root_view.parent as View)
        mViewModel.parseReply(comment)
        et_publish_editor.hint = context.getString(R.string.str_comment_reply_hint) + comment.nickName

        editor_check_btn.setText(R.string.editor_also_repost)
        mViewModel.updateChecked(CheckState(true, false))
        iv_expand.setOnClickListener {
            expand()
        }

        btn_editor_send.setOnClickListener {
            performSendClick()
        }

        et_publish_editor.maxLines = 3
        et_publish_editor.setPadding(ScreenUtils.dip2Px(15f), 0,
                ScreenUtils.dip2Px(15f), ScreenUtils.dip2Px(10f))
        et_publish_editor.layoutParams.height = ScreenUtils.dip2Px(64f)
        editor_root_view.setOnTouchListener(this)
//        btn_editor_link.isEnabled = false

        mViewModel.mSendBtnStateLiveData.observe(context, Observer {
            if (isExpand)
                return@Observer

            btn_editor_send.isEnabled = it ?: false
            if (mViewModel.isTextOverLimit) {
                et_publish_editor_component.setBackgroundResource(R.drawable.bg_comment_input_state2)
            } else {
                et_publish_editor_component.setBackgroundResource(R.drawable.bg_comment_input_state1)
            }
        })
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {

        if (v == editor_root_view) {
            if (event?.action == MotionEvent.ACTION_DOWN) {
                KeyboardUtil.hideKeyboard(currentFocus)
                this.cancel()
                return false
            }
        }
        return super.onTouch(v, event)

    }


    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        cancel()
        return false
    }


    private fun expand() {
        editor_root_view.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        bottomSheetBehavior.peekHeight = ScreenUtils.getScreenHeight(context)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//        drag_view.visibility = View.GONE

        isExpand = true
//        btn_editor_link.isEnabled = true
        et_publish_editor.maxLines = Integer.MAX_VALUE
        et_publish_editor.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        et_publish_editor.setPadding(ScreenUtils.dip2Px(15f), 0,
                ScreenUtils.dip2Px(15f), ScreenUtils.dip2Px(50f))

        publish_toolbar_component.visibility = View.VISIBLE
        iv_expand.visibility = View.INVISIBLE
        btn_editor_send.visibility = View.GONE
        val editorLayoutParams: ViewGroup.LayoutParams = et_publish_editor_component.layoutParams
        editorLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        et_publish_editor_component.layoutParams = editorLayoutParams

        val scrollViewLayoutParams = editor_scroll_view.layoutParams as RelativeLayout.LayoutParams
        scrollViewLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        editor_scroll_view.layoutParams = scrollViewLayoutParams
        scrollViewLayoutParams.addRule(RelativeLayout.BELOW, R.id.publish_toolbar_component)

        editor_root_view.setBackgroundColor(context.resources.getColor(R.color.editor_root_view_bg))
        et_publish_editor_component.setBackgroundResource(R.color.editor_root_view_bg)

        val layoutParams = editor_post_text_limit_warn.layoutParams
                as RelativeLayout.LayoutParams
        layoutParams.addRule(RelativeLayout.RIGHT_OF, 0)
        layoutParams.addRule(RelativeLayout.ALIGN_RIGHT, R.id.iv_expand)
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
        editor_post_text_limit_warn.layoutParams = layoutParams
    }

}