package com.redefine.welike.business.publisher.ui.dialog

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatCheckBox
import android.text.TextUtils
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil
import cn.dreamtobe.kpswitch.util.KeyboardUtil
import com.redefine.commonui.dialog.OnMenuItemClickListener
import com.redefine.foundation.utils.InputMethodUtil
import com.redefine.richtext.RichContent
import com.redefine.richtext.emoji.EmojiPanel
import com.redefine.richtext.emoji.bean.EmojiBean
import com.redefine.richtext.processor.RichEditTextProcessor
import com.redefine.welike.R
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.base.track.AFGAEventManager
import com.redefine.welike.base.track.TrackerConstant
import com.redefine.welike.business.publisher.calculateEventData
import com.redefine.welike.business.publisher.e
import com.redefine.welike.business.publisher.management.BrowsePublishManager
import com.redefine.welike.business.publisher.management.PublishAnalyticsManager
import com.redefine.welike.business.publisher.management.PublisherEventManager
import com.redefine.welike.business.publisher.management.bean.CheckState
import com.redefine.welike.business.publisher.management.bean.DraftBase
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean
import com.redefine.welike.business.publisher.ui.activity.DraftActivity
import com.redefine.welike.business.publisher.ui.contract.IContactListContract
import com.redefine.welike.business.publisher.viewmodel.AbsCommentViewModel
import com.redefine.welike.business.startup.management.constant.RegisteredConstant
import com.redefine.welike.business.startup.ui.activity.RegistActivity
import com.redefine.welike.business.user.management.bean.User
import com.redefine.welike.commonui.event.model.PublishEventModel
import com.redefine.welike.commonui.view.ActionSnackBar
import com.redefine.welike.statistical.EventLog
import com.redefine.welike.statistical.EventLog1
import kotlinx.android.synthetic.main.activity_publish_comment_popup.*
import kotlinx.android.synthetic.main.layout_publish_post_editor_input.*
import kotlinx.android.synthetic.main.layout_publish_tools_and_emotion_popup.*
import kotlinx.android.synthetic.main.toolbar_publish_common.*

/**
 * @author qingfei.chen
 * @date 2018/11/24
 * Copyright (C) 2018 redefine , Inc.
 */
open abstract class AbsPublishDialog<D : DraftBase, VM : AbsCommentViewModel<D>>(val context: AppCompatActivity, val draftId: String) : BottomSheetDialog(context, R.style.BottomSheetEdit),
        View.OnTouchListener, View.OnClickListener,
        KPSwitchConflictUtil.SwitchClickListener,
        EmojiPanel.OnEmojiItemClickListener,
        RichEditTextProcessor.OnInputRichFlagListener,
        OnTopicChoiceListener,
        IContactListContract.OnContactChoiceListener {
    protected lateinit var mPublishEventModel: PublishEventModel

    lateinit var mViewModel: VM

    abstract fun getViewModel(): VM

    abstract fun getRichContent(): RichContent

    abstract fun getEventLabel(): String

    abstract fun getLimitWarnView(): TextView

    abstract fun getCheckBoxView(): AppCompatCheckBox


    lateinit var mCheckBox: AppCompatCheckBox
    var isExpand = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewModel = getViewModel()
        mPublishEventModel = PublishAnalyticsManager.getInstance().obtainEventModel(draftId)
        mPublishEventModel.proxy.report1()


        mCheckBox = getCheckBoxView()
        et_publish_editor_component.attach(getLimitWarnView(), mViewModel)
        registerListener()
        registerObserve()
        e("publisher", javaClass.simpleName + "onCreate")
    }

    fun onDestroy() {

        mPublishEventModel.proxy.report14()
    }

    override fun onClick(v: View?) {
        when (v) {

            tv_btn_draft_save ->
                showDraftSaveDialog()
            iv_btn_back ->
                performBackClick()

//            btn_editor_at ->
//                performAtClick()
//            btn_editor_link ->
//                performLinkClick()
//            btn_editor_topic ->
//                performTopicClick()
            tv_btn_send ->
                performSendClick()
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP
                || event?.action == MotionEvent.ACTION_CANCEL) {

            KeyboardUtil.showKeyboard(currentFocus)
        }
        return false
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (!isExpand) {
            return super.dispatchKeyEvent(event)
        }
        if (event.action == KeyEvent.ACTION_UP && event.keyCode == KeyEvent.KEYCODE_BACK) {
            if (editor_bottom_emoji_container.visibility == View.VISIBLE) {
                KPSwitchConflictUtil.hidePanelAndKeyboard(editor_bottom_emoji_container)
                return true
            }

            val canBack = performBackClick()
            if (!canBack) {
                return true
            }
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onClickSwitch(v: View, switchToPanel: Boolean) {

        if (switchToPanel) {
//            btn_editor_emoji.isSelected = false
            currentFocus.clearFocus()
        } else {
//            btn_editor_emoji.isSelected = true
            currentFocus.requestFocus()
        }

//        if (editor_bottom_emoji_container.visibility == View.VISIBLE) {
//            editor_bottom_emoji_container.visibility = View.GONE
//            KeyboardUtil.showKeyboard(currentFocus)
//        } else {
//            editor_bottom_emoji_container.visibility = View.VISIBLE
//            KeyboardUtil.hideKeyboard(currentFocus)
//        }


        EventLog.Publish.report10(PublisherEventManager.INSTANCE.source,
                PublisherEventManager.INSTANCE.main_source,
                PublisherEventManager.INSTANCE.page_type)
        AFGAEventManager.getInstance().sendEventWithLabel(TrackerConstant.EVENT_POSTER_EMOJI,
                getEventLabel())
        mPublishEventModel.proxy.report10()
    }


    override fun onEmojiClick(emojiBean: EmojiBean?) {
        mViewModel.updateEmotion(emojiBean)
    }

    override fun onEmojiDelClick() {
        mViewModel.updateEmotion(null)
    }

    override fun onMentionInput() {
        onClickAt()
    }

    override fun onTopicInput() {
        onClickTopic()
    }

    private fun performBackClick(): Boolean {
        InputMethodUtil.hideInputMethod(currentFocus)

        if (!AccountManager.getInstance().isLogin) {
            BrowsePublishManager.getInstance().setDraftBase(null)
            cancel()
            return true
        }

        if (!mViewModel.mDraft?.isSaveDB || !tv_btn_draft_save.isEnabled) {
            cancel()
            return true
        }
        showDraftSaveDialog()
        return false
    }

    private fun showDraftSaveDialog() {
        if (!AccountManager.getInstance().isLogin) {
            mViewModel.updateLoginState(false)
            return
        }
        DraftSaveMenuDialog(OnMenuItemClickListener {
            when (it?.menuId) {
                DraftSaveMenuDialog.MENU_ITEM_SAVE -> {
                    mPublishEventModel.existState = EventLog1.Publish.ExistState.SAVE_DRAFT
                    PublisherEventManager.INSTANCE.exit_state = 2
                    mViewModel.saveDraft(getSubmitText())
                    cancel()
                    DraftActivity.launch(context)
                }
                DraftSaveMenuDialog.MENU_ITEM_DISCARD -> {
                    mPublishEventModel.existState = EventLog1.Publish.ExistState.DISCARD
                    cancel()
                }

            }
        }, context).showDialog()
    }

    private fun registerListener() {
        tv_btn_draft_save.isEnabled = false
        iv_btn_back.setOnClickListener(this)
        tv_btn_draft_save.setOnClickListener(this)
//        btn_editor_at.setOnClickListener(this)
//        btn_editor_link.setOnClickListener(this)
//        btn_editor_topic.setOnClickListener(this)
        tv_btn_send.setOnClickListener(this)

        editor_scroll_view.setOnTouchListener(this)
        editor_emoji_layout.setOnEmojiItemClickListener(this)
        mCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            mViewModel.updateChecked(CheckState(isChecked, true))
        }

        initEmojiListener(et_publish_editor)
        et_publish_editor.richProcessor.setOnInputMentionListener(this)
    }


    private fun getSubmitText(): RichContent {
        val richContent = getRichContent()
        calculateEventData(richContent,mPublishEventModel)
        return richContent
    }

    private fun registerObserve() {
//        mViewModel.mDraftSaveBtnStateLiveData.observe(context, Observer {
////            tv_btn_draft_save.isEnabled = it ?: false
////        })

        mViewModel.mSendBtnStateLiveData.observe(context, Observer {
            tv_btn_send.isEnabled = it ?: false
        })

        mViewModel.mTopicBtnStateLiveData.observe(context, Observer {
//            btn_editor_topic.isSelected = it ?: false
        })

        mViewModel.mCheckedLiveData.observe(context, Observer { checkedState ->
            checkedState?.let {
                if (!it.isClick) {
                    mCheckBox.isChecked = it.checked
                }
            }
        })

        mViewModel.mLoginStateLiveData.observe(context, Observer {
            InputMethodUtil.hideInputMethod(et_publish_editor_component)
            val info: String = context.getString(R.string.common_continue_by_login)
            ActionSnackBar.getInstance().showLoginSnackBar(et_publish_editor_component, info,
                    context.getString(R.string.common_login), 3000) {
                RegistActivity.show(context, RegisteredConstant.FRAGMENT_PHONENUM, null)
            }
        })
    }

    private fun initEmojiListener(currentFocus: View) {
//        KeyboardUtil.attach(context, editor_bottom_emoji_container) { isShowing ->
//            if (isShowing) {
//                btn_editor_emoji.isSelected = true
//            } else {
//                btn_editor_emoji.isSelected =
//                        editor_bottom_emoji_container.visibility != View.VISIBLE
//            }
//        }
//
//
//        KPSwitchConflictUtil.attach(editor_bottom_emoji_container,
//                btn_editor_emoji, currentFocus, this)


//        btn_editor_emoji.setOnClickListener {
//            //            if (switchToPanel) {
////                btn_editor_emoji.isSelected = false
////                currentFocus.clearFocus()
////            } else {
////                btn_editor_emoji.isSelected = true
////                currentFocus.requestFocus()
////            }
//
//            if (editor_bottom_emoji_container.visibility == View.VISIBLE) {
//                editor_bottom_emoji_container.visibility = View.GONE
//                btn_editor_emoji.isSelected = false
//                KeyboardUtil.showKeyboard(currentFocus)
//            } else {
//                KeyboardUtil.hideKeyboard(currentFocus)
//                editor_bottom_emoji_container.visibility = View.VISIBLE
//                btn_editor_emoji.isSelected = true
//            }
//        }
    }

    private fun performAtClick() {
        onClickAt()
        AFGAEventManager.getInstance().sendEventWithLabel(TrackerConstant.EVENT_POSTER_AT,
                getEventLabel())
        EventLog.Publish.report3(PublisherEventManager.INSTANCE.source,
                PublisherEventManager.INSTANCE.main_source,
                PublisherEventManager.INSTANCE.page_type)
        mPublishEventModel.proxy.report3()
    }

    private fun performLinkClick() {
        InputMethodUtil.hideInputMethod(currentFocus)
        AddLinkDialog.showDialog(context, object : OnLinkSubmitListener {
            override fun onLinkAdd(link: String) {

                if (!TextUtils.isEmpty(link)) {
                    mViewModel.updateLink(link)
                }
            }
        })
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

    protected fun performSendClick() {
        InputMethodUtil.hideInputMethod(currentFocus)
        AFGAEventManager.getInstance().sendEventWithLabel(TrackerConstant.EVENT_POSTER_SEND,
                getEventLabel())
        mPublishEventModel.proxy.report13()
        val ok = mViewModel.send(getSubmitText())
        if (!ok) {
            return
        }
        PublisherEventManager.INSTANCE.exit_state = 1
        mPublishEventModel.existState = EventLog1.Publish.ExistState.SEND
        cancel()
    }

    private fun onClickTopic() {
//        if (btn_editor_topic.isSelected) {
//            ToastUtils.showShort(ResourceTool.getString("topic_count_over_limit"))
//        } else {
//            InputMethodUtil.hideInputMethod(currentFocus)
////            TopicChoiceActivity.launch(this, CommonRequestCode.CHOICE_TOPIC_CODE)
//            TopicChoiceDialog.showDialog(context, false, this)
//        }
    }

    private fun onClickAt() {
        if (!AccountManager.getInstance().isLogin) {
            mViewModel.updateLoginState(false)
            return
        }
        InputMethodUtil.hideInputMethod(currentFocus)
//        ContactListActivity.launch(this, CommonRequestCode.EDITOR_LAUNCH_CONTACT_CODE)
        ContactListDialog.showDialog(context, this)
    }

    override fun onTopicChoice(topicbean: TopicSearchSugBean.TopicBean?, isWithFlag: Boolean) {
        topicbean ?: return

        PublishAnalyticsManager.getInstance().obtainCurrentModel().topicSource = EventLog1.Publish.TopicSource.CHOOSE_SUG_TOPIC
        mViewModel.updateTopic(topicbean, isWithFlag)
    }

    override fun onUserChoice(user: User?) {
        user?.let {
            mViewModel.updateMention(user.nickName, user.uid, false)
        }
    }
}