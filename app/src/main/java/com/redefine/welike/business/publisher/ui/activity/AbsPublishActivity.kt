package com.redefine.welike.business.publisher.ui.activity

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil
import cn.dreamtobe.kpswitch.util.KeyboardUtil
import com.redefine.commonui.activity.BaseActivity
import com.redefine.commonui.dialog.OnMenuItemClickListener
import com.redefine.foundation.utils.InputMethodUtil
import com.redefine.richtext.RichContent
import com.redefine.richtext.emoji.EmojiPanel
import com.redefine.richtext.emoji.bean.EmojiBean
import com.redefine.richtext.processor.RichEditTextProcessor
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.base.track.AFGAEventManager
import com.redefine.welike.base.track.TrackerConstant
import com.redefine.welike.business.feeds.ui.constant.FeedConstant
import com.redefine.welike.business.publisher.calculateEventData
import com.redefine.welike.business.publisher.management.BrowsePublishManager
import com.redefine.welike.business.publisher.management.PublishAnalyticsManager
import com.redefine.welike.business.publisher.management.PublisherEventManager
import com.redefine.welike.business.publisher.management.bean.DraftBase
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean
import com.redefine.welike.business.publisher.ui.bean.BottomBehaviorItem
import com.redefine.welike.business.publisher.ui.component.BottomBehaviorView
import com.redefine.welike.business.publisher.ui.component.EditorInputView
import com.redefine.welike.business.publisher.ui.component.OnItemClickListener
import com.redefine.welike.business.publisher.ui.component.base.LayoutResource
import com.redefine.welike.business.publisher.ui.contract.IContactListContract
import com.redefine.welike.business.publisher.ui.dialog.*
import com.redefine.welike.business.publisher.viewmodel.AbsPublishViewModel
import com.redefine.welike.business.startup.management.HalfLoginManager
import com.redefine.welike.business.user.management.bean.User
import com.redefine.welike.commonui.event.model.PublishEventModel
import com.redefine.welike.statistical.EventLog
import com.redefine.welike.statistical.EventLog1
import com.redefine.welike.statistical.bean.RegisterAndLoginModel
import kotlinx.android.synthetic.main.layout_publish_tools_and_emotion.*
import kotlinx.android.synthetic.main.toolbar_publish_common.*

/**
 * @author qingfei.chen
 * @date 2018/11/24
 * Copyright (C) 2018 redefine , Inc.
 */
abstract class AbsPublishActivity<D : DraftBase, VM : AbsPublishViewModel<D>> : BaseActivity(),
        View.OnTouchListener, View.OnClickListener,
        EmojiPanel.OnEmojiItemClickListener,
        RichEditTextProcessor.OnInputRichFlagListener,
        OnTopicChoiceListener,
        IContactListContract.OnContactChoiceListener,
        KeyboardUtil.OnKeyboardShowingListener,
        OnItemClickListener {

    protected lateinit var mPublishEventModel: PublishEventModel
    lateinit var mViewModel: VM
    lateinit var mDraftId: String

    abstract fun getViewModel(): VM
    abstract fun getRichContent(): RichContent
    abstract fun getEventLabel(): String
    abstract fun getLimitWarnView(): TextView
    abstract fun getEditorInputView(): EditorInputView
    abstract fun getBottomBehaviorView(): BottomBehaviorView
    abstract fun getScrollView(): View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = javaClass.getAnnotation(LayoutResource::class.java)
        setContentView(layout.layout)
        tv_title.setText(layout.title)

        mDraftId = intent.getStringExtra(EXTRA_DRAFT_ID)
        mViewModel = getViewModel()
        mPublishEventModel = PublishAnalyticsManager.getInstance().obtainEventModel(mDraftId)
        mPublishEventModel.proxy.report1()
        registerListener()
        registerObserve()

        getEditorInputView().attach(getLimitWarnView(), mViewModel)

        intent.getSerializableExtra(FeedConstant.KEY_DRAFT)?.let {
            mViewModel.restoreDraft(it as D)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPublishEventModel.proxy.report14()
    }

    override fun onClick(v: View?) {
        when (v) {
            tv_btn_draft_save -> {
                KeyboardUtil.hideKeyboard(getCurrentFocusView())
                if (!AccountManager.getInstance().isLogin) {
                    mViewModel.updateLoginState(false)
                    return
                }

                if (!mViewModel.mDraftEnable) {
                    mPublishEventModel.existState = EventLog1.Publish.ExistState.SAVE_DRAFT
                    if (!AccountManager.getInstance().isLogin) {
                        mViewModel.updateLoginState(false)
                        return
                    }
                    this.finish()
                    DraftActivity.launch(this)
                    return
                }
                showDraftSaveDialog(false)
            }
            iv_btn_back ->
                performBackClick()
            tv_btn_send ->
                performSendClick()

        }
    }

    override fun onItemClick(item: BottomBehaviorItem?) {
        if (item == null) {
            return
        }

        when (item.type) {
            BottomBehaviorItem.BEHAVIOR_EMOJI_TYPE -> {
                KPSwitchConflictUtil
                        .switchPanelAndKeyboard(editor_bottom_emoji_container, currentFocus)

                if(editor_bottom_emoji_container.visibility != View.VISIBLE) {
                    EventLog.Publish.report10(PublisherEventManager.INSTANCE.source,
                            PublisherEventManager.INSTANCE.main_source,
                            PublisherEventManager.INSTANCE.page_type)
                    AFGAEventManager.getInstance().sendEventWithLabel(TrackerConstant.EVENT_POSTER_EMOJI,
                            getEventLabel())
                    mPublishEventModel.proxy.report10()
                }
            }
            BottomBehaviorItem.BEHAVIOR_AT_TYPE -> {
                performAtClick()
            }

            BottomBehaviorItem.BEHAVIOR_LINK_TYPE -> {
                performLinkClick()
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP
                || event?.action == MotionEvent.ACTION_CANCEL) {

            KeyboardUtil.showKeyboard(getCurrentFocusView())
        }
        return false
    }

    override fun onKeyboardShowing(isKeyboardShowing: Boolean) {
        val isEmotionShow = editor_bottom_emoji_container.visibility == View.VISIBLE
        getBottomBehaviorView().toggleSize(isKeyboardShowing || isEmotionShow)
        getBottomBehaviorView().setItemSelected(!isEmotionShow, BottomBehaviorItem.BEHAVIOR_EMOJI_TYPE)



    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_UP && event.keyCode == KeyEvent.KEYCODE_BACK) {
            if (editor_bottom_emoji_container.visibility == View.VISIBLE) {
                KPSwitchConflictUtil.hidePanelAndKeyboard(editor_bottom_emoji_container)
                return true
            }

            if (!AccountManager.getInstance().isLogin) {
                BrowsePublishManager.getInstance().setDraftBase(null)
                finish()
            }

            val canBack = performBackClick()
            if (!canBack) {
                return true
            }
        }
        return super.dispatchKeyEvent(event)
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

    open fun registerListener() {
        iv_btn_back.setOnClickListener(this)
        tv_btn_draft_save.setOnClickListener(this)
        tv_btn_send.setOnClickListener(this)
        getScrollView().setOnTouchListener(this)
        editor_emoji_layout.setOnEmojiItemClickListener(this)
        KeyboardUtil.attach(this, editor_bottom_emoji_container, this)

        getEditorInputView().getRichEditText().richProcessor.setOnInputMentionListener(this)
    }

    fun getCurrentFocusView(): View {
        if (currentFocus != null) {
            return currentFocus
        }
        return getEditorInputView()
    }

    private fun performBackClick(): Boolean {
        InputMethodUtil.hideInputMethod(getCurrentFocusView())

        if (!AccountManager.getInstance().isLogin) {
            BrowsePublishManager.getInstance().setDraftBase(null)
            finish()
            return true
        }

        if (!mViewModel.mDraft?.isSaveDB || !mViewModel.mDraftEnable) {
            finish()
            return true
        }
        showDraftSaveDialog(true)
        return false
    }

    open fun showDraftSaveDialog(isBackAction: Boolean) {
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
                    finish()
                    if (!isBackAction) {
                        DraftActivity.launch(this)
                    }
                }
                DraftSaveMenuDialog.MENU_ITEM_DISCARD -> {
                    mPublishEventModel.existState = EventLog1.Publish.ExistState.DISCARD
                    getSubmitText()
                    finish()
                }

            }
        }, this).showDialog()
    }

    fun getSubmitText(): RichContent {
        val richContent = getRichContent()
        calculateEventData(richContent, mPublishEventModel)
        return richContent
    }

    open fun registerObserve() {

        mViewModel.mSendBtnStateLiveData.observe(this, Observer {
            tv_btn_send.isEnabled = it ?: false
        })

        mViewModel.mLoginStateLiveData.observe(this, Observer {
            InputMethodUtil.hideInputMethod(getCurrentFocusView())
            HalfLoginManager.getInstancce().showLoginDialog(this,
                    RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.COMMENT))
        })
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
        AddLinkDialog.showDialog(this, object : OnLinkSubmitListener {
            override fun onLinkAdd(link: String) {

                if (!TextUtils.isEmpty(link)) {
                    mViewModel.updateLink(link)
                }
            }
        })
    }

    protected fun performSendClick() {
        InputMethodUtil.hideInputMethod(getCurrentFocusView())
        AFGAEventManager.getInstance().sendEventWithLabel(TrackerConstant.EVENT_POSTER_SEND,
                getEventLabel())
        mPublishEventModel.proxy.report13()
        val ok = mViewModel.send(getSubmitText())
        if (!ok) {
            return
        }
        PublisherEventManager.INSTANCE.exit_state = 1
        mPublishEventModel.existState = EventLog1.Publish.ExistState.SEND
        finish()
    }

    private fun onClickAt() {
        if (!AccountManager.getInstance().isLogin) {
            mViewModel.updateLoginState(false)
            return
        }
        InputMethodUtil.hideInputMethod(getCurrentFocusView())
        ContactListDialog.showDialog(this, this)
    }

    override fun onTopicChoice(topicbean: TopicSearchSugBean.TopicBean?, isWithFlag: Boolean) {
        showInputMethod()
        topicbean ?: return

        PublishAnalyticsManager.getInstance().obtainCurrentModel().topicSource =
                EventLog1.Publish.TopicSource.CHOOSE_SUG_TOPIC
        mViewModel.updateTopic(topicbean, isWithFlag)

    }

    override fun onUserChoice(user: User?) {
        showInputMethod()
        user?.let {
            mViewModel.updateMention(user.nickName, user.uid, false)
        }
    }

    private fun showInputMethod() {
        if (!getBottomBehaviorView().getEmojiView()?.isSelected!!) {
            return
        }
        getCurrentFocusView().let {
            it.postDelayed({
                InputMethodUtil.showInputMethod(it)
            }, 200)
        }
    }
}