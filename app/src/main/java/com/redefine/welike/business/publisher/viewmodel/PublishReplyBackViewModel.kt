package com.redefine.welike.business.publisher.viewmodel

import android.text.TextUtils
import com.redefine.richtext.RichContent
import com.redefine.welike.base.GlobalConfig
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.business.feeds.management.bean.Comment
import com.redefine.welike.business.publisher.management.FeedReplyReplier
import com.redefine.welike.business.publisher.management.PublishAnalyticsManager
import com.redefine.welike.business.publisher.management.PublisherEventManager
import com.redefine.welike.business.publisher.management.bean.CheckState
import com.redefine.welike.business.publisher.management.bean.DraftReplyBack
import com.redefine.welike.commonui.event.commonenums.BooleanValue
import com.redefine.welike.hive.AppsFlyerManager

/**
 * @author qingfei.chen
 * @date 2018/11/7
 * Copyright (C) 2018 redefine , Inc.
 */
class PublishReplyBackViewModel : AbsCommentViewModel<DraftReplyBack>() {

    override fun newDraftInstance(): DraftReplyBack {
        return DraftReplyBack()
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

    override fun publish(draft: DraftReplyBack?) {
        val account = AccountManager.getInstance().account
        if (account == null || TextUtils.isEmpty(account.uid)) return
        val commentSender = FeedReplyReplier()
        commentSender.publish(draft)

        AppsFlyerManager.addEvent(AppsFlyerManager.EVENT_POST)
        PublisherEventManager.INSTANCE.also_repost = if (mCheckedLiveData.value?.checked == true) 1 else 0
        PublisherEventManager.INSTANCE.report14()

        val mPublishEventModel = PublishAnalyticsManager.getInstance().obtainEventModel(mDraft.draftId)
        mPublishEventModel.contentUid = mDraft.contentUid
        mPublishEventModel.alsoRepost = if (mCheckedLiveData.value?.checked == true) BooleanValue.YES else BooleanValue.NO
    }

    override fun buildDraft(richContent: RichContent?): DraftReplyBack? {
        richContent ?: return null

        mDraft.content = richContent
        mDraft.isAsRepost = mCheckedLiveData.value?.checked ?: true
        return mDraft
    }

    override fun restoreDraft(draft: DraftReplyBack?) {
        if (draft == null) {
            return
        }

        mDraft = draft

        updateRichContent(draft.content)
        updateChecked(CheckState(draft.isAsRepost, false))
    }

    fun parseDraftReply(comment: Comment?, reply: Comment?) {
        comment ?: return
        reply ?: return
        mDraft.pid = comment.pid
        mDraft.cid = comment.cid
        mDraft.replyId = reply.cid
        mDraft.contentUid = comment.uid

        mDraft.commentNick = reply.nickName
        mDraft.commentUid = reply.uid


        val cContent = RichContent()
        cContent.summary = reply.content
        cContent.text = reply.content
        cContent.richItemList = reply.richItemList
        mDraft.rcontent = cContent

        mDraft.content = null

        mDraft.isAsRepost = false
        updateRichContent(mDraft.content)
        updateChecked(CheckState(mDraft.isAsRepost, false))
    }

}