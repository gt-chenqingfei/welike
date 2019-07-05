package com.redefine.welike.business.publisher.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.text.TextUtils
import com.redefine.richtext.RichContent
import com.redefine.welike.base.GlobalConfig
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.base.resource.ResourceTool
import com.redefine.welike.business.feeds.management.bean.Comment
import com.redefine.welike.business.feeds.management.bean.ForwardPost
import com.redefine.welike.business.feeds.management.bean.PostBase
import com.redefine.welike.business.feeds.ui.util.FeedHelper
import com.redefine.welike.business.publisher.management.FeedReposter
import com.redefine.welike.business.publisher.management.PublishAnalyticsManager
import com.redefine.welike.business.publisher.management.PublisherEventManager
import com.redefine.welike.business.publisher.management.bean.CheckState
import com.redefine.welike.business.publisher.management.bean.DraftForwardPost
import com.redefine.welike.commonui.event.commonenums.BooleanValue
import com.redefine.welike.hive.AppsFlyerManager

/**
 * @author qingfei.chen
 * @date 2018/11/7
 * Copyright (C) 2018 redefine , Inc.
 */
class PublishForwardViewModel : AbsCommentViewModel<DraftForwardPost>() {

    val mForwardPostLiveData: MutableLiveData<PostBase> = MutableLiveData()

    override fun newDraftInstance(): DraftForwardPost {
        return DraftForwardPost()
    }

    override fun afterTextChanged(textLength: Int, text: CharSequence,
                                  topicCount: Int, hasFocus: Boolean,
                                  superTopicCount: Int) {
        isTextOverLimit = textLength > GlobalConfig.SUMMARY_LIMIT
        isTextEmpty = TextUtils.getTrimmedLength(text) == 0

        val topicBtnDisable = topicCount >= GlobalConfig.FEED_MAX_TOPIC
        updateTopicBtnState(topicBtnDisable)

        var sendBtnEnable = true

        if (isTextOverLimit) {
            sendBtnEnable = false
        }

        updateSendBtnState(sendBtnEnable)
        updateDraftSaveBtnState(sendBtnEnable)
    }

    override fun publish(draft: DraftForwardPost?) {
        val account = AccountManager.getInstance().account
        if (account == null || TextUtils.isEmpty(account.uid)) return

        val commentSender = FeedReposter()
        commentSender.publish(draft)

        AppsFlyerManager.addEvent(AppsFlyerManager.EVENT_FORWARD)
        PublisherEventManager.INSTANCE.also_repost =
                if (mCheckedLiveData.value?.checked == true) 1 else 0
        PublisherEventManager.INSTANCE.report14()

        val mPublishEventModel =
                PublishAnalyticsManager.getInstance().obtainEventModel(mDraft.draftId)
        mPublishEventModel.contentUid = mDraft.contentUid
        mPublishEventModel.sequenceId = mDraft.sequenceId
        mPublishEventModel.alsoComment =
                if (mCheckedLiveData.value?.checked == true) BooleanValue.YES else BooleanValue.NO
    }

    override fun buildDraft(richContent: RichContent?): DraftForwardPost? {
        richContent ?: return null

        if (TextUtils.isEmpty(richContent.text)) {
            val content = ResourceTool.getString("publish_reply_post_empty_content")
            richContent.text = content
            richContent.summary = content
        }

        mDraft.content = richContent
        mDraft.isAsComment = mCheckedLiveData.value?.checked ?: true
        return mDraft
    }

    override fun restoreDraft(draft: DraftForwardPost?) {
        if (draft == null) {
            return
        }

        mDraft = draft

        updateForward(draft.rootPost)
        updateRichContent(draft.content)
        updateChecked(CheckState(draft.isAsComment, false))
        updateSendBtnState(true)
        updateDraftSaveBtnState(true)
    }

    fun parseForwardComment(postBase: PostBase?, comment: Comment?) {
        postBase ?: return

        comment ?: return

        var nickName = comment.nickName ?: ""
        mDraft.content = RichContentHelper.buildForwardRichContent(comment.uid,
                nickName, comment.content, comment.richItemList)

        mDraft.rootPost = postBase
        if (postBase is ForwardPost) {
            mDraft.isForwardDeleted = postBase.isForwardDeleted
        }
        mDraft.isAsComment = true
        updateForward(postBase)
        updateRichContent(mDraft.content)
        updateChecked(CheckState(mDraft.isAsComment, false))
        updateSendBtnState(true)
        updateDraftSaveBtnState(true)
    }

    fun parseFrowardPost(postBase: PostBase?) {
        postBase ?: return

        mDraft.rootPost = postBase
        if (postBase is ForwardPost) {
            mDraft.isForwardDeleted = postBase.isForwardDeleted
            mDraft.content = RichContentHelper.buildForwardRichContent(postBase.getUid(),
                    postBase.getNickName(),
                    postBase.getText(), postBase.getRichItemList())
        }

        mDraft.contentUid = FeedHelper.getRootOrPostUid(postBase)
        mDraft.sequenceId = postBase.sequenceId
        mDraft.commentPid = postBase.pid
        mDraft.isAsComment = true
        updateForward(postBase)
        updateRichContent(mDraft.content)
        updateChecked(CheckState(mDraft.isAsComment, false))
        updateSendBtnState(true)
        updateDraftSaveBtnState(true)
    }

    private fun updateForward(postBase: PostBase?) {
        mForwardPostLiveData.postValue(postBase)
    }

}