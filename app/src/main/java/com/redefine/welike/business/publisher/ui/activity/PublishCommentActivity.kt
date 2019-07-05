package com.redefine.welike.business.publisher.ui.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import android.widget.TextView
import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil
import com.redefine.foundation.utils.InputMethodUtil
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
import kotlinx.android.synthetic.main.activity_publish_comment.*
import kotlinx.android.synthetic.main.layout_publish_comment_check_and_limit_warn.*
import kotlinx.android.synthetic.main.layout_publish_post_editor_input.*
import kotlinx.android.synthetic.main.layout_publish_tools_and_emotion.*

/**
 * @author qingfei.chen
 * @date 2018/11/20
 * Copyright (C) 2018 redefine , Inc.
 */
@LayoutResource(layout = R.layout.activity_publish_comment, title = R.string.editor_comment_title)
class PublishCommentActivity : AbsPublishActivity<DraftComment,
        PublishCommentViewModel>(), View.OnTouchListener {
    override fun getBottomBehaviorView(): BottomBehaviorView {
        return editor_bottom_behavior_view
    }

    override fun getScrollView(): View {
        return editor_scroll_view
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
                .getRichContent(true, -1,
                        GlobalConfig.PUBLISH_COMMENT_INPUT_TEXT_MAX_OVER_LIMIT)
    }

    override fun getEventLabel(): String {
        return "comment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let { intent ->

            intent.getSerializableExtra(FeedConstant.KEY_POST_BASE)?.let {
                val postBase = it as PostBase
                mViewModel.parseComment(postBase)
            }


            val isEmojiDisplay = intent.getBooleanExtra(FeedConstant.KEY_EMOJI_DISPLAY, false)
            if (isEmojiDisplay) {
                editor_bottom_emoji_container.postDelayed({
                    KPSwitchConflictUtil.showPanel(editor_bottom_emoji_container)
                    editor_bottom_behavior_view.setItemSelected(true,BottomBehaviorItem.BEHAVIOR_EMOJI_TYPE)
                }, 100)

            } else {
                editor_bottom_emoji_container.postDelayed({
                    InputMethodUtil.showInputMethod(currentFocus)
                }, 100)
            }
        }

        editor_check_btn.setText(R.string.editor_also_repost)
        editor_check_btn.setOnCheckedChangeListener { buttonView, isChecked ->
            mViewModel.updateChecked(CheckState(isChecked, true))
        }
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