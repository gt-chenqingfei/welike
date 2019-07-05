package com.redefine.welike.business.publisher.ui.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import cn.dreamtobe.kpswitch.util.KeyboardUtil
import com.redefine.foundation.utils.InputMethodUtil
import com.redefine.foundation.utils.ScreenUtils
import com.redefine.richtext.RichContent
import com.redefine.welike.R
import com.redefine.welike.base.GlobalConfig
import com.redefine.welike.base.track.AFGAEventManager
import com.redefine.welike.base.track.TrackerConstant
import com.redefine.welike.business.feeds.management.bean.PostBase
import com.redefine.welike.business.feeds.ui.constant.FeedConstant
import com.redefine.welike.business.publisher.management.PublishAnalyticsManager
import com.redefine.welike.business.publisher.management.PublisherEventManager
import com.redefine.welike.business.publisher.management.bean.CheckState
import com.redefine.welike.business.publisher.management.bean.DraftCategory
import com.redefine.welike.business.publisher.management.bean.DraftComment
import com.redefine.welike.business.publisher.ui.bean.BottomBehaviorItem
import com.redefine.welike.business.publisher.ui.component.BottomBehaviorView
import com.redefine.welike.business.publisher.ui.component.EditorInputView
import com.redefine.welike.business.publisher.ui.component.base.LayoutResource
import com.redefine.welike.business.publisher.ui.dialog.TopicChoiceDialog
import com.redefine.welike.business.publisher.viewmodel.PublishCommentViewModel
import com.redefine.welike.commonui.util.ToastUtils
import com.redefine.welike.statistical.EventLog
import kotlinx.android.synthetic.main.activity_publish_comment_popup.*
import kotlinx.android.synthetic.main.layout_publish_comment_check_and_limit_warn_popup.*
import kotlinx.android.synthetic.main.layout_publish_post_editor_input.*
import kotlinx.android.synthetic.main.layout_publish_tools_and_emotion_popup.*
import kotlinx.android.synthetic.main.toolbar_publish_common.*

/**
 * @author qingfei.chen
 * @date 2018/11/20
 * Copyright (C) 2018 redefine , Inc.
 */
@LayoutResource(layout = R.layout.activity_publish_comment_popup, title = R.string.editor_comment_title)
class PublishCommentPopUpActivity : AbsPublishActivity<DraftComment, PublishCommentViewModel>(), View.OnTouchListener {
    private var isExpand = false

    override fun getScrollView(): View {
        return editor_scroll_view
    }

    override fun getBottomBehaviorView(): BottomBehaviorView {
        return editor_bottom_behavior_view
    }

    override fun getEditorInputView(): EditorInputView {
        return et_publish_editor_component
    }

    override fun getLimitWarnView(): TextView {
        return editor_post_text_limit_warn
    }

    override fun getViewModel(): PublishCommentViewModel {
        val vm = ViewModelProviders.of(this)
                .get(PublishCommentViewModel::class.java)
        vm.init(DraftCategory.COMMENT, mDraftId)
        return vm
    }

    override fun getRichContent(): RichContent {
        return et_publish_editor.richProcessor
                .getRichContent(true, -1, GlobalConfig.PUBLISH_COMMENT_INPUT_TEXT_MAX_OVER_LIMIT)
    }

    override fun getEventLabel(): String {
        return "comment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.dialog_enter, 0)
        super.onCreate(savedInstanceState)

        intent?.let { intent ->

            intent.getSerializableExtra(FeedConstant.KEY_POST_BASE)?.let {
                val postBase = it as PostBase
                mViewModel.parseComment(postBase)
            }

        }

        iv_btn_back.setImageResource(R.drawable.common_close)
        editor_check_btn.setText(R.string.editor_also_repost)
        et_publish_editor.setHint(R.string.str_write_a_comment)

        iv_expand.setOnClickListener {
            expand()
        }

        btn_editor_send.setOnClickListener {
            performSendClick()
        }

        editor_check_btn.setOnCheckedChangeListener { buttonView, isChecked ->
            mViewModel.updateChecked(CheckState(isChecked, true))
        }

        mViewModel.updateChecked(CheckState(false, false))
        et_publish_editor.maxLines = 3
        et_publish_editor.setPadding(ScreenUtils.dip2Px(15f), 0,
                ScreenUtils.dip2Px(15f), ScreenUtils.dip2Px(10f))
        et_publish_editor.layoutParams.height = ScreenUtils.dip2Px(64f)
        editor_root_view.setOnTouchListener(this)
        editor_bottom_behavior_view.setOnItemClickListener(this)
    }

    override fun registerObserve() {
        super.registerObserve()
        mViewModel.mTopicBtnStateLiveData.observe(this, Observer {
            editor_bottom_behavior_view.setItemSelected(it
                    ?: false, BottomBehaviorItem.BEHAVIOR_TOPIC_ITEM_TYPE)
        })

        mViewModel.mCheckedLiveData.observe(this, Observer { checkedState ->
            checkedState?.let {
                if (!it.isClick) {
                    editor_check_btn.isChecked = it.checked
                }
            }
        })

        mViewModel.mSendBtnStateLiveData.observe(this, Observer {
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

    override fun finish() {
        super.finish()
        window.setDimAmount(0f)
        overridePendingTransition(0, R.anim.dialog_exit)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_UP && event.keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {

        if (v == editor_root_view) {
            if (event?.action == MotionEvent.ACTION_DOWN) {
                KeyboardUtil.hideKeyboard(getCurrentFocusView())
                this.finish()
                return false
            }
        }
        return super.onTouch(v, event)
    }


    private fun expand() {
        isExpand = true
        drag_view.visibility = View.GONE
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

        editor_root_view.setBackgroundColor(resources.getColor(R.color.editor_root_view_bg))
        et_publish_editor_component.setBackgroundResource(R.color.editor_root_view_bg)

        val layoutParams = editor_post_text_limit_warn.layoutParams
                as RelativeLayout.LayoutParams
        layoutParams.addRule(RelativeLayout.RIGHT_OF, 0)
        layoutParams.addRule(RelativeLayout.ALIGN_RIGHT, R.id.iv_expand)
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
        editor_post_text_limit_warn.layoutParams = layoutParams
    }

    override fun onTopicInput() {
        onClickTopic()
    }

    override fun onItemClick(item: BottomBehaviorItem?) {
        super.onItemClick(item)
        if (item?.type == BottomBehaviorItem.BEHAVIOR_TOPIC_ITEM_TYPE) {
            performTopicClick()
        }
    }

    private fun performTopicClick() {
        onClickTopic()
        AFGAEventManager.getInstance().sendEventWithLabel(TrackerConstant.EVENT_POSTER_HASHTAG,
                getEventLabel())
        EventLog.Publish.report19(PublisherEventManager.INSTANCE.source,
                PublisherEventManager.INSTANCE.main_source,
                PublisherEventManager.INSTANCE.page_type)
        PublishAnalyticsManager.getInstance().obtainCurrentModel().proxy.report20()
    }

    private fun onClickTopic() {
        if (editor_bottom_behavior_view.getTopicView()?.isSelected!!) {
            ToastUtils.showShort(getString(R.string.topic_count_over_limit))
        } else {
            InputMethodUtil.hideInputMethod(getCurrentFocusView())
            TopicChoiceDialog.showDialog(this, false, this)
        }
    }


}