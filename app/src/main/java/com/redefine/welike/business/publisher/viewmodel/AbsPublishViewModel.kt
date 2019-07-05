package com.redefine.welike.business.publisher.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.redefine.richtext.RichContent
import com.redefine.richtext.emoji.bean.EmojiBean
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.business.publisher.api.bean.CommonTopic
import com.redefine.welike.business.publisher.api.bean.SuperTopic
import com.redefine.welike.business.publisher.management.BrowsePublishManager
import com.redefine.welike.business.publisher.management.DraftManager
import com.redefine.welike.business.publisher.management.PublishSubject
import com.redefine.welike.business.publisher.management.bean.DraftBase
import com.redefine.welike.business.publisher.management.bean.MentionWrapper
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean
import com.redefine.welike.business.publisher.management.bean.TopicWrapper
import com.redefine.welike.business.publisher.management.draft.ArticleDraft

/**
 * @author qingfei.chen
 * @date 2018/11/20
 * Copyright (C) 2018 redefine , Inc.
 */
abstract class AbsPublishViewModel<T : DraftBase> : ViewModel(), PublishSubject<T> {
    val mMentionLiveData: MutableLiveData<MentionWrapper> = MutableLiveData()
    val mTopicLiveData: MutableLiveData<TopicWrapper> = MutableLiveData()
    val mSuperTopicLiveData: MutableLiveData<SuperTopic> = MutableLiveData()
    val mCommonTopicLiveData: MutableLiveData<CommonTopic> = MutableLiveData()
    val mLinkLiveData: MutableLiveData<String> = MutableLiveData()
    val mContentLiveData: MutableLiveData<String> = MutableLiveData()
    val mEmotionLiveData: MutableLiveData<EmojiBean> = MutableLiveData()
    val mRichContentLiveData: MutableLiveData<RichContent> = MutableLiveData()
    val mArticleLiveData: MutableLiveData<ArticleDraft> = MutableLiveData()
    val mSendBtnStateLiveData: MutableLiveData<Boolean> = MutableLiveData()
    //    val mDraftSaveBtnStateLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val mTopicBtnStateLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val mLoginStateLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var mDraftEnable: Boolean = false

    lateinit var mDraft: T
    lateinit var mProxy: PublishSubject<T>
    protected abstract fun newDraftInstance(): T

    protected abstract fun buildDraft(richContent: RichContent?): T?

    abstract fun afterTextChanged(textLength: Int, text: CharSequence, topicCount: Int,
                                  hasFocus: Boolean,
                                  superTopicCount: Int)

    abstract fun getInputTextMaxOverLimit(): Int
    abstract fun getInputTextMaxWarnLimit(): Int

    abstract fun restoreDraft(draft: T?)

    fun init(type: Int, draftId: String) {
        mDraft = generateDraft(type, draftId)
        mProxy = this
    }

    fun init(type: Int, draftId: String, proxy: PublishSubject<T>) {
        init(type, draftId)
        mProxy = proxy
    }

    fun updateSendBtnState(enabled: Boolean) {
        mSendBtnStateLiveData.postValue(enabled)
    }

    fun updateDraftSaveBtnState(enabled: Boolean) {
        mDraftEnable = AccountManager.getInstance().isLogin && enabled
    }

    fun updateTopicBtnState(enabled: Boolean) {
        mTopicBtnStateLiveData.postValue(enabled)
    }

    fun updateLink(linkUrl: String?) {
        mLinkLiveData.postValue(linkUrl)
    }

    fun updateContent(content: String?) {
        mContentLiveData.postValue(content)
    }

    fun updateTopic(topic: TopicSearchSugBean.TopicBean, withOutFlag: Boolean) {
        mTopicLiveData.postValue(TopicWrapper(topic, withOutFlag = withOutFlag))
    }

    fun updateSuperTopic(topic: SuperTopic) {
        mSuperTopicLiveData.postValue(topic)
    }

    fun updateCommonTopic(topic: CommonTopic) {
        mCommonTopicLiveData.postValue(topic)
    }

    fun updateEmotion(emotion: EmojiBean?) {
        mEmotionLiveData.postValue(emotion)
    }

    fun updateMention(mention: String?, uid: String?, withOutFlag: Boolean) {
        mMentionLiveData.postValue(MentionWrapper(mention, uid, withOutFlag))
    }

    fun updateRichContent(richContent: RichContent?) {
        mRichContentLiveData.postValue(richContent)
    }

    fun updateLoginState(loginState: Boolean) {
        mLoginStateLiveData.postValue(loginState)
    }

    fun saveDraft(richContent: RichContent?) {
        val draftPost = buildDraft(richContent)
        draftPost?.isShow = true
        draftPost?.let {
            performDraftSave(it)
        }
    }

    private fun performDraftSave(draft: T?) {
        draft ?: return
        DraftManager.getInstance().insertOrUpdate(draft)
    }

    fun send(richContent: RichContent?): Boolean {
        val draftPost = buildDraft(richContent)
        draftPost?.let {
            if (!AccountManager.getInstance().isLogin) {
                BrowsePublishManager.getInstance().setDraftBase(draftPost)
                updateLoginState(false)
                return false
            }
//            performDraftSave(it)
            mProxy.publish(it)
        }
        return true
    }

    private fun generateDraft(type: Int, draftId: String): T {
        val postDraft = newDraftInstance()
        postDraft.draftId = draftId
        postDraft.type = type
        postDraft.isShow = false
        return postDraft
    }


}