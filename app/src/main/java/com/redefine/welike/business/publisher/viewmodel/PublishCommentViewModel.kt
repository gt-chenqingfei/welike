package com.redefine.welike.business.publisher.viewmodel

import android.text.TextUtils
import com.redefine.richtext.RichContent
import com.redefine.welike.base.GlobalConfig
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.business.feeds.management.bean.ForwardPost
import com.redefine.welike.business.feeds.management.bean.PostBase
import com.redefine.welike.business.feeds.ui.util.FeedHelper
import com.redefine.welike.business.publisher.management.FeedCommentSender
import com.redefine.welike.business.publisher.management.PublishAnalyticsManager
import com.redefine.welike.business.publisher.management.PublisherEventManager
import com.redefine.welike.business.publisher.management.bean.DraftComment
import com.redefine.welike.commonui.event.commonenums.BooleanValue
import com.redefine.welike.hive.AppsFlyerManager

/**
 * @author qingfei.chen
 * @date 2018/11/7
 * Copyright (C) 2018 redefine , Inc.
 */
class PublishCommentViewModel : AbsCommentViewModel<DraftComment>() {

    override fun newDraftInstance(): DraftComment {
        return DraftComment()
    }

    override fun afterTextChanged(textLength: Int, text: CharSequence, topicCount: Int, hasFocus: Boolean,
                                  superTopicCount: Int) {
        isTextOverLimit = textLength > GlobalConfig.SUMMARY_LIMIT
        isTextEmpty = TextUtils.getTrimmedLength(text) == 0

        val topicBtnDisable = topicCount >= GlobalConfig.FEED_MAX_TOPIC
        updateTopicBtnState(topicBtnDisable)

        var sendBtnEnable = !isTextEmpty

        if (isTextOverLimit) {
            sendBtnEnable = false
        }

        updateSendBtnState(sendBtnEnable)
        updateDraftSaveBtnState(!isTextEmpty)
    }

    override fun publish(draft: DraftComment?) {
        val account = AccountManager.getInstance().account
        if (account == null || TextUtils.isEmpty(account.uid)) return
        val commentSender = FeedCommentSender()
        commentSender.publish(draft)

        AppsFlyerManager.addEvent(AppsFlyerManager.EVENT_COMMENT)
        PublisherEventManager.INSTANCE.also_repost = if (mCheckedLiveData.value?.checked == true) 1 else 0
        PublisherEventManager.INSTANCE.report14()

        val mPublishEventModel = PublishAnalyticsManager.getInstance().obtainEventModel(mDraft.draftId)
        mPublishEventModel.contentUid = mDraft.contentUid
        mPublishEventModel.sequenceId = mDraft.sequenceId
        mPublishEventModel.alsoRepost = if (mCheckedLiveData.value?.checked == true) BooleanValue.YES else BooleanValue.NO
    }

    override fun buildDraft(richContent: RichContent?): DraftComment? {
        richContent ?: return null

        mDraft.content = richContent
        mDraft.isAsRepost = mCheckedLiveData.value?.checked ?: false

        return mDraft
    }


    override fun restoreDraft(draft: DraftComment?) {
        if (draft == null) {
            return
        }

        mDraft = draft

        updateRichContent(draft.content)
    }

    fun parseComment(postBase: PostBase) {
        if (postBase is ForwardPost) {
            val postContent = RichContent()
            postContent.richItemList = postBase.getRichItemList()
            postContent.summary = postBase.getSummary()
            postContent.text = postBase.getText()
            mDraft.fcontent = postContent
        }
        mDraft.contentUid = FeedHelper.getRootOrPostUid(postBase)
        mDraft.commentNick = postBase.nickName
        mDraft.commentUid = postBase.uid
        mDraft.pid = postBase.pid
        mDraft.content = null
        mDraft.sequenceId = postBase.sequenceId
    }


}