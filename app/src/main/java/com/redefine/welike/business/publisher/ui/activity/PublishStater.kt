package com.redefine.welike.business.publisher.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import com.alibaba.fastjson.JSONObject
import com.redefine.foundation.utils.CommonHelper
import com.redefine.richtext.RichContent
import com.redefine.welike.R
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.business.easypost.EasyPostActivity
import com.redefine.welike.business.feeds.management.bean.Comment
import com.redefine.welike.business.feeds.management.bean.PostBase
import com.redefine.welike.business.feeds.management.parser.PostsDataSourceParser
import com.redefine.welike.business.feeds.ui.constant.FeedConstant
import com.redefine.welike.business.publisher.management.PublishAnalyticsManager
import com.redefine.welike.business.publisher.management.PublisherEventManager
import com.redefine.welike.business.publisher.management.bean.*
import com.redefine.welike.business.publisher.management.draft.ArticleDraft
import com.redefine.welike.business.startup.management.HalfLoginManager
import com.redefine.welike.commonui.event.model.PostStatusModel
import com.redefine.welike.statistical.EventLog
import com.redefine.welike.statistical.EventLog1
import com.redefine.welike.statistical.bean.RegisterAndLoginModel
import java.net.URLDecoder

/**
 * @author qingfei.chen
 * @date 2018/11/26
 * Copyright (C) 2018 redefine , Inc.
 */
const val EXTRA_DRAFT_ID: String = "extra_draft_id"

object PublishPostStarter {
    const val EXTRA_FROM: String = "extra_from"
    const val EXTRA_PUBLISH_POLL_POSITION = "poll_item_position"

    fun startActivityFromMain(context: Context, mainSource: EventLog1.Publish.MainSource) {
        val intent = Intent(context, PublishPostActivity::class.java)
        val draftId = CommonHelper.generateUUID()
        intent.putExtra(EXTRA_DRAFT_ID, draftId)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)
        Analytics.log(draftId, mainSource,
                EventLog1.Publish.Source.MAIN_PAGE, EventLog1.Publish.PageType.NEW_POST)
    }

    fun startActivityFromMain(context: Context, bundle: Bundle) {
        val intent = Intent(context, PublishPostActivity::class.java)
        val draftId = CommonHelper.generateUUID()
        intent.putExtra(EXTRA_DRAFT_ID, draftId)
        intent.putExtras(bundle)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)
        Analytics.log(draftId, EventLog1.Publish.MainSource.HOME,
                EventLog1.Publish.Source.MAIN_PAGE, EventLog1.Publish.PageType.NEW_POST)
    }

    fun startActivityFromBrowseMain(context: Context) {
        val intent = Intent(context, PublishPostActivity::class.java)
        val draftId = CommonHelper.generateUUID()
        intent.putExtra(EXTRA_DRAFT_ID, draftId)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)
        Analytics.log(draftId, EventLog1.Publish.MainSource.OTHER,
                EventLog1.Publish.Source.MAIN_PAGE, EventLog1.Publish.PageType.NEW_POST)
    }


    fun startActivityFromTopic(context: Context, bundle: Bundle) {
        val intent = Intent()
        intent.putExtras(bundle)
        val draftId = CommonHelper.generateUUID()
        intent.putExtra(EXTRA_DRAFT_ID, draftId)
        intent.setClass(context, PublishPostActivity::class.java)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)
        Analytics.log(draftId, EventLog1.Publish.MainSource.OTHER,
                EventLog1.Publish.Source.TOPIC_OR_SUPER_TOPIC, EventLog1.Publish.PageType.NEW_POST)
    }

    fun startActivityFromDraft(context: Context, draftPost: DraftPost) {
        val intent = Intent()

        intent.putExtra(EXTRA_DRAFT_ID, draftPost.draftId)
        intent.putExtra(FeedConstant.KEY_DRAFT, draftPost)
        intent.setClass(context, PublishPostActivity::class.java)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)

        Analytics.log(draftPost.draftId, EventLog1.Publish.MainSource.OTHER,
                EventLog1.Publish.Source.DRAFT, EventLog1.Publish.PageType.NEW_POST)
    }

    /**
     * @param context      上下文
     * @param content      发布内容的正文
     * @param picDraftList 图片附件 必须的参数  localFileName,  mimeType, height, width
     */
    fun startActivityWithImage(context: Context, content: String?,
                               picDraftList: List<DraftPicAttachment>, from: String?) {
        val intent = Intent()
        val draftId = CommonHelper.generateUUID()
        intent.putExtra(EXTRA_DRAFT_ID, draftId)
        val draft = DraftPost()
        content?.let {
            val richContent = RichContent()
            richContent.summary = it
            richContent.text = it
            draft.content = richContent
        }
        draft.picDraftList = picDraftList
        intent.putExtra(FeedConstant.KEY_DRAFT, draft)
        intent.putExtra(EXTRA_FROM, from)
        intent.setClass(context, PublishPostActivity::class.java)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)

        Analytics.log(draftId, EventLog1.Publish.MainSource.OUT_SHARE,
                EventLog1.Publish.Source.MAIN_PAGE, EventLog1.Publish.PageType.NEW_POST)
    }

    /**
     * @param context              上下文
     * @param content              发布内容的正文
     * @param videoAttachmentDraft 视频附件 必须的参数  localFileName,duration,  mimeType, height, width
     */
    fun startActivityWithVideo(context: Context, content: String?,
                               videoAttachmentDraft: DraftVideoAttachment, from: String?) {
        val intent = Intent()
        val draftId = CommonHelper.generateUUID()
        intent.putExtra(EXTRA_DRAFT_ID, draftId)
        val draft = DraftPost()
        content?.let {
            val richContent = RichContent()
            richContent.summary = it
            richContent.text = it
            draft.content = richContent
        }
        draft.video = videoAttachmentDraft
        intent.putExtra(FeedConstant.KEY_DRAFT, draft)
        intent.putExtra(EXTRA_FROM, from)
        intent.setClass(context, PublishPostActivity::class.java)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)

        Analytics.log(draftId, EventLog1.Publish.MainSource.OUT_SHARE,
                EventLog1.Publish.Source.MAIN_PAGE, EventLog1.Publish.PageType.NEW_POST)
    }

    fun startActivityFromOutShare(context: Context, bundle: Bundle) {
        val intent = Intent()
        intent.putExtras(bundle)
        val draftId = CommonHelper.generateUUID()
        intent.putExtra(EXTRA_DRAFT_ID, draftId)
        intent.setClass(context, PublishPostActivity::class.java)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)
        Analytics.log(draftId, EventLog1.Publish.MainSource.OUT_SHARE,
                EventLog1.Publish.Source.MAIN_PAGE, EventLog1.Publish.PageType.NEW_POST)
    }

    fun startActivityFromArticle(context: Context, articleDraft: ArticleDraft?) {
        val intent = Intent()
        val post = DraftPost()
        val draftId = CommonHelper.generateUUID()
        intent.putExtra(EXTRA_DRAFT_ID, CommonHelper.generateUUID())
        post.article = articleDraft
        intent.putExtra(FeedConstant.KEY_DRAFT, post)
        intent.setClass(context, PublishPostActivity::class.java)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in, R.anim.sliding_to_left_out)
        Analytics.log(draftId, EventLog1.Publish.MainSource.OTHER,
                EventLog1.Publish.Source.ARTICLE, EventLog1.Publish.PageType.NEW_POST)
    }
}

object PublishCommentStarter {

    fun startPopupActivityFromFeedDetail(context: AppCompatActivity, postBase: PostBase) {
        val intent = Intent(context, PublishCommentPopUpActivity::class.java)
        val draftId = CommonHelper.generateUUID()
        intent.putExtra(EXTRA_DRAFT_ID, draftId)
        intent.putExtra(FeedConstant.KEY_POST_BASE, postBase)
        context.startActivity(intent)
        Analytics.log(draftId, EventLog1.Publish.MainSource.OTHER,
                EventLog1.Publish.Source.POST_DETAIL, EventLog1.Publish.PageType.COMMENT)

    }

    fun startPopupActivityFromCard(context: AppCompatActivity, postBase: PostBase) {
        val intent = Intent(context, PublishCommentPopUpActivity::class.java)
        val draftId = CommonHelper.generateUUID()
        intent.putExtra(EXTRA_DRAFT_ID, draftId)
        intent.putExtra(FeedConstant.KEY_POST_BASE, postBase)
        context.startActivity(intent)
        Analytics.log(draftId, EventLog1.Publish.MainSource.OTHER,
                EventLog1.Publish.Source.POST_CARD, EventLog1.Publish.PageType.COMMENT)
    }

    fun startActivityFromArticleDetail(context: Context, postBase: PostBase) {
        val intent = Intent(context, PublishCommentActivity::class.java)
        intent.putExtra(FeedConstant.KEY_POST_BASE, postBase)
        val draftId = CommonHelper.generateUUID()
        intent.putExtra(EXTRA_DRAFT_ID, draftId)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)

        Analytics.log(draftId, EventLog1.Publish.MainSource.OTHER,
                EventLog1.Publish.Source.ARTICLE, EventLog1.Publish.PageType.COMMENT)
    }

    fun startActivityFromFeedDetail(context: Context, postBase: PostBase) {
        val intent = Intent(context, PublishCommentActivity::class.java)
        intent.putExtra(FeedConstant.KEY_POST_BASE, postBase)
        val draftId = CommonHelper.generateUUID()
        intent.putExtra(EXTRA_DRAFT_ID, draftId)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)

        Analytics.log(draftId, EventLog1.Publish.MainSource.OTHER,
                EventLog1.Publish.Source.POST_DETAIL, EventLog1.Publish.PageType.COMMENT)
    }

    fun startActivityFromNotification(context: Context, postBase: PostBase) {
        val intent = Intent(context, PublishCommentActivity::class.java)
        intent.putExtra(FeedConstant.KEY_POST_BASE, postBase)
        val draftId = CommonHelper.generateUUID()
        intent.putExtra(EXTRA_DRAFT_ID, draftId)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)
        Analytics.log(draftId, EventLog1.Publish.MainSource.OTHER,
                EventLog1.Publish.Source.MAIN_PAGE, EventLog1.Publish.PageType.COMMENT)
    }


    fun startActivityFromEmoji(context: Context, postBase: PostBase) {
        val intent = Intent(context, PublishCommentActivity::class.java)
        val draftId = CommonHelper.generateUUID()
        intent.putExtra(EXTRA_DRAFT_ID, draftId)
        intent.putExtra(FeedConstant.KEY_POST_BASE, postBase)
        intent.putExtra(FeedConstant.KEY_EMOJI_DISPLAY, true)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)
        Analytics.log(draftId, EventLog1.Publish.MainSource.OTHER,
                EventLog1.Publish.Source.COMMENT_EMOJI, EventLog1.Publish.PageType.COMMENT)
    }

    fun startActivityFromCard(context: Context, postBase: PostBase) {
        val intent = Intent(context, PublishCommentActivity::class.java)
        val draftId = CommonHelper.generateUUID()
        intent.putExtra(EXTRA_DRAFT_ID, draftId)
        intent.putExtra(FeedConstant.KEY_POST_BASE, postBase)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)
        Analytics.log(draftId, EventLog1.Publish.MainSource.OTHER,
                EventLog1.Publish.Source.POST_CARD, EventLog1.Publish.PageType.COMMENT)
    }

    fun startActivityFromVideoDetail(context: Context, postBase: PostBase) {
        val intent = Intent(context, PublishCommentActivity::class.java)
        val draftId = CommonHelper.generateUUID()
        intent.putExtra(EXTRA_DRAFT_ID, draftId)
        intent.putExtra(FeedConstant.KEY_POST_BASE, postBase)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)
        Analytics.log(draftId, EventLog1.Publish.MainSource.OTHER,
                EventLog1.Publish.Source.POST_CARD, EventLog1.Publish.PageType.COMMENT)
    }

    fun startActivityFromDraft(context: Context, draft: DraftComment) {
        val intent = Intent()
        intent.putExtra(EXTRA_DRAFT_ID, draft.draftId)
        intent.putExtra(FeedConstant.KEY_DRAFT, draft)
        intent.setClass(context, PublishCommentActivity::class.java)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)
        Analytics.log(draft.draftId, EventLog1.Publish.MainSource.OTHER,
                EventLog1.Publish.Source.DRAFT, EventLog1.Publish.PageType.COMMENT)
    }
}

object PublishForwardStarter {
    const val EXTRA_FORWARD_TYPE: String = "forward_type"
    const val EXTRA_COMMENT: String = "extra_comment"
    const val FORWARD_TYPE_POST: Int = 0
    const val FORWARD_TYPE_COMMENT: Int = 1

    fun startActivity4PostFromArticle(context: Context, postBase: PostBase) {
        if (!AccountManager.getInstance().isLogin) {
            HalfLoginManager.getInstancce().showLoginDialog(context, RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.FORWARD))
            return
        }

        val intent = Intent(context, PublishForwardActivity::class.java)
        val draftId = CommonHelper.generateUUID()
        intent.putExtra(EXTRA_DRAFT_ID, draftId)
        intent.putExtra(FeedConstant.KEY_POST_BASE, postBase)
        intent.putExtra(EXTRA_FORWARD_TYPE, FORWARD_TYPE_POST)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)
        Analytics.log(draftId, EventLog1.Publish.MainSource.OTHER,
                EventLog1.Publish.Source.ARTICLE, EventLog1.Publish.PageType.REPOST)
    }

    fun startActivity4PostFromVidmate(context: Context, bundle: Bundle?) {
        if (!AccountManager.getInstance().isLogin) {
            HalfLoginManager.getInstancce().showLoginDialog(context, RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.FORWARD))
            return
        }

        if (bundle == null) {
            return
        }

        var content = bundle.getString("data")
        if (TextUtils.isEmpty(content)) {
            return
        }

        content = URLDecoder.decode(content)

        var postBase: PostBase
        try {
            val jsonObj: JSONObject = JSONObject.parseObject(content)
            postBase = PostsDataSourceParser.parsePostBase(jsonObj)
        } catch (e: Exception) {
            com.redefine.welike.business.publisher.e("PublishForwardStater", "fromJson error:" + e.toString())
            return
        }


        val intent = Intent(context, PublishForwardActivity::class.java)
        val draftId = CommonHelper.generateUUID()
        intent.putExtra(EXTRA_DRAFT_ID, draftId)
        intent.putExtra(FeedConstant.KEY_POST_BASE, postBase)
        intent.putExtra(EXTRA_FORWARD_TYPE, FORWARD_TYPE_POST)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)
        Analytics.log(draftId, EventLog1.Publish.MainSource.OTHER,
                EventLog1.Publish.Source.ARTICLE, EventLog1.Publish.PageType.REPOST)
    }

    fun startActivity4PostFromFeedDetail(context: Context, postBase: PostBase) {
        if (!AccountManager.getInstance().isLogin) {
            HalfLoginManager.getInstancce().showLoginDialog(context, RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.FORWARD))
            return
        }
        val intent = Intent(context, PublishForwardActivity::class.java)
        val draftId = CommonHelper.generateUUID()
        intent.putExtra(EXTRA_DRAFT_ID, draftId)
        intent.putExtra(FeedConstant.KEY_POST_BASE, postBase)
        intent.putExtra(EXTRA_FORWARD_TYPE, FORWARD_TYPE_POST)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)
        Analytics.log(draftId, EventLog1.Publish.MainSource.OTHER,
                EventLog1.Publish.Source.POST_DETAIL, EventLog1.Publish.PageType.REPOST)
    }

    fun startActivity4PostFromFeedCard(context: Context, postBase: PostBase) {
        if (!AccountManager.getInstance().isLogin) {
            HalfLoginManager.getInstancce().showLoginDialog(context, RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.FORWARD))
            return
        }

        val intent = Intent(context, PublishForwardActivity::class.java)
        val draftId = CommonHelper.generateUUID()
        intent.putExtra(EXTRA_DRAFT_ID, draftId)
        intent.putExtra(FeedConstant.KEY_POST_BASE, postBase)
        intent.putExtra(EXTRA_FORWARD_TYPE, FORWARD_TYPE_POST)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)
        Analytics.log(draftId, EventLog1.Publish.MainSource.OTHER,
                EventLog1.Publish.Source.POST_CARD, EventLog1.Publish.PageType.REPOST)
    }

    fun startActivity4PostFromNotification(context: Context, postBase: PostBase) {
        if (!AccountManager.getInstance().isLogin) {
            HalfLoginManager.getInstancce().showLoginDialog(context, RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.FORWARD))
            return
        }
        val intent = Intent(context, PublishForwardActivity::class.java)
        val draftId = CommonHelper.generateUUID()
        intent.putExtra(EXTRA_DRAFT_ID, draftId)
        intent.putExtra(FeedConstant.KEY_POST_BASE, postBase)
        intent.putExtra(EXTRA_FORWARD_TYPE, FORWARD_TYPE_POST)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)
        Analytics.log(draftId, EventLog1.Publish.MainSource.OTHER,
                EventLog1.Publish.Source.MAIN_PAGE, EventLog1.Publish.PageType.REPOST)
    }

    fun startActivity4CommentFromCommentDetail(context: Context, postBase: PostBase, comment: Comment) {
        if (!AccountManager.getInstance().isLogin) {
            HalfLoginManager.getInstancce().showLoginDialog(context, RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.FORWARD))
            return
        }
        val intent = Intent(context, PublishForwardActivity::class.java)
        intent.putExtra(FeedConstant.KEY_POST_BASE, postBase)
        val draftId = CommonHelper.generateUUID()
        intent.putExtra(EXTRA_DRAFT_ID, draftId)
        intent.putExtra(EXTRA_FORWARD_TYPE, FORWARD_TYPE_COMMENT)
        intent.putExtra(EXTRA_COMMENT, comment)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)
        Analytics.log(draftId, EventLog1.Publish.MainSource.OTHER,
                EventLog1.Publish.Source.COMMENT_DETAIL, EventLog1.Publish.PageType.REPOST)
    }

    fun startActivity4CommentFromFeedDetail(context: Context, postBase: PostBase, comment: Comment) {
        if (!AccountManager.getInstance().isLogin) {
            HalfLoginManager.getInstancce().showLoginDialog(context, RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.FORWARD))
            return
        }
        val intent = Intent(context, PublishForwardActivity::class.java)
        intent.putExtra(FeedConstant.KEY_POST_BASE, postBase)
        val draftId = CommonHelper.generateUUID()
        intent.putExtra(EXTRA_DRAFT_ID, draftId)
        intent.putExtra(EXTRA_FORWARD_TYPE, FORWARD_TYPE_COMMENT)
        intent.putExtra(EXTRA_COMMENT, comment)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)
        Analytics.log(draftId, EventLog1.Publish.MainSource.OTHER,
                EventLog1.Publish.Source.POST_DETAIL, EventLog1.Publish.PageType.REPOST)
    }

    fun startActivity4CommentFromNotification(context: Context, postBase: PostBase, comment: Comment) {
        if (!AccountManager.getInstance().isLogin) {
            HalfLoginManager.getInstancce().showLoginDialog(context, RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.FORWARD))
            return
        }
        val intent = Intent(context, PublishForwardActivity::class.java)
        intent.putExtra(FeedConstant.KEY_POST_BASE, postBase)
        val draftId = CommonHelper.generateUUID()
        intent.putExtra(EXTRA_DRAFT_ID, draftId)
        intent.putExtra(EXTRA_FORWARD_TYPE, FORWARD_TYPE_COMMENT)
        intent.putExtra(EXTRA_COMMENT, comment)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)
        Analytics.log(draftId, EventLog1.Publish.MainSource.OTHER,
                EventLog1.Publish.Source.MAIN_PAGE, EventLog1.Publish.PageType.REPOST)
    }

    fun startActivityFromDraft(context: Context, draft: DraftForwardPost) {
        val intent = Intent()
        intent.putExtra(FeedConstant.KEY_DRAFT, draft)
        intent.putExtra(EXTRA_DRAFT_ID, draft.draftId)
        intent.setClass(context, PublishForwardActivity::class.java)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)
        Analytics.log(draft.draftId, EventLog1.Publish.MainSource.OTHER,
                EventLog1.Publish.Source.DRAFT, EventLog1.Publish.PageType.REPOST)
    }

}

object PublishReplyBackStarter {

    const val EXTRA_COMMENT: String = "extra_comment"
    const val EXTRA_RREPLY: String = "extra_reply"

    fun startActivityFromCommentDetail(context: Context, reply: Comment, comment: Comment) {
        val intent = Intent(context, PublishReplyBackActivity::class.java)
        val draftId = CommonHelper.generateUUID()
        intent.putExtra(EXTRA_DRAFT_ID, draftId)
        intent.putExtra(EXTRA_COMMENT, comment)
        intent.putExtra(EXTRA_RREPLY, reply)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)
        Analytics.log(draftId, EventLog1.Publish.MainSource.OTHER,
                EventLog1.Publish.Source.COMMENT_DETAIL, EventLog1.Publish.PageType.REPLY)
    }

    fun startActivityFromNotification(context: Context, reply: Comment, comment: Comment) {
        val intent = Intent(context, PublishReplyBackActivity::class.java)
        val draftId = CommonHelper.generateUUID()
        intent.putExtra(EXTRA_DRAFT_ID, draftId)
        intent.putExtra(EXTRA_COMMENT, comment)
        intent.putExtra(EXTRA_RREPLY, reply)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)
        Analytics.log(draftId, EventLog1.Publish.MainSource.OTHER,
                EventLog1.Publish.Source.MAIN_PAGE, EventLog1.Publish.PageType.REPLY)
    }

    fun startActivityFromDraft(context: Context, draft: DraftReplyBack) {
        val intent = Intent()
        intent.putExtra(EXTRA_DRAFT_ID, draft.draftId)
        intent.putExtra(FeedConstant.KEY_DRAFT, draft)
        intent.setClass(context, PublishReplyBackActivity::class.java)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)
        Analytics.log(draft.draftId, EventLog1.Publish.MainSource.OTHER,
                EventLog1.Publish.Source.DRAFT, EventLog1.Publish.PageType.REPLY)
    }

}

object PublishReplyStarter {
    const val EXTRA_COMMENT: String = "extra_comment"

    fun startPopUpActivityFromCommentDetail(context: AppCompatActivity, comment: Comment) {
        val intent = Intent(context, PublishReplyPopUpActivity::class.java)
        val draftId = CommonHelper.generateUUID()
        intent.putExtra(EXTRA_DRAFT_ID, draftId)
        intent.putExtra(EXTRA_COMMENT, comment)
        context.startActivity(intent)
        Analytics.log(draftId, EventLog1.Publish.MainSource.OTHER,
                EventLog1.Publish.Source.COMMENT_DETAIL, EventLog1.Publish.PageType.REPLY)
    }

    fun startActivityFromCommentDetail(context: Context, comment: Comment) {
        val intent = Intent(context, PublishReplyActivity::class.java)
        val draftId = CommonHelper.generateUUID()
        intent.putExtra(EXTRA_DRAFT_ID, draftId)
        intent.putExtra(EXTRA_COMMENT, comment)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)

        Analytics.log(draftId, EventLog1.Publish.MainSource.OTHER,
                EventLog1.Publish.Source.COMMENT_DETAIL, EventLog1.Publish.PageType.REPLY)
    }

    fun startActivityFromFeedDetail(context: Context, comment: Comment) {
        val intent = Intent(context, PublishReplyActivity::class.java)
        val draftId = CommonHelper.generateUUID()
        intent.putExtra(EXTRA_DRAFT_ID, draftId)
        intent.putExtra(EXTRA_COMMENT, comment)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)

        Analytics.log(draftId, EventLog1.Publish.MainSource.OTHER,
                EventLog1.Publish.Source.POST_DETAIL, EventLog1.Publish.PageType.REPLY)
    }

    fun startActivityFromDraft(context: Context, draft: DraftReply) {
        val intent = Intent()
        intent.putExtra(EXTRA_DRAFT_ID, draft.draftId)
        intent.putExtra(FeedConstant.KEY_DRAFT, draft)
        intent.setClass(context, PublishReplyActivity::class.java)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)
        Analytics.log(draft.draftId, EventLog1.Publish.MainSource.OTHER,
                EventLog1.Publish.Source.COMMENT_DETAIL, EventLog1.Publish.PageType.REPLY)
    }

}

object PostStatusStater {

    const val EXTRA_EVENT_MODEL = "event_model"
    const val EXTRA_DRAFT_ID = "draft_id"
    fun startActivityFromEdit(context: Context) {
        val intent = Intent()
        val draftId = CommonHelper.generateUUID()
        intent.setClass(context, EasyPostActivity::class.java)
        intent.putExtra(EXTRA_EVENT_MODEL,
                PostStatusModel(EventLog1.PostStatus.ButtonFrom.PUBLISHER_ENTRANCE))
        intent.putExtra(EXTRA_DRAFT_ID,draftId)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)

        Analytics.log(draftId, EventLog1.Publish.MainSource.OTHER,
                EventLog1.Publish.Source.MAIN_PAGE, EventLog1.Publish.PageType.POST_STATUS)
    }

    fun startActivityFromGuide(context: Context) {
        val intent = Intent()
        val draftId = CommonHelper.generateUUID()
        intent.setClass(context, EasyPostActivity::class.java)
        intent.putExtra(EXTRA_EVENT_MODEL,
                PostStatusModel(EventLog1.PostStatus.ButtonFrom.GUIDE_ANIMATION))
        intent.putExtra(EXTRA_DRAFT_ID,draftId)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.sliding_right_in,
                R.anim.sliding_to_left_out)
        Analytics.log(draftId, EventLog1.Publish.MainSource.OTHER,
                EventLog1.Publish.Source.MAIN_PAGE, EventLog1.Publish.PageType.POST_STATUS)
    }
}


object PublishDraftStarter {

    fun <T : DraftBase> startActivityWidthDraft(context: Context, draft: T) {
        when {
            draft is DraftPost ->
                PublishPostStarter.startActivityFromDraft(context, draft as DraftPost)
            draft is DraftComment ->
                PublishCommentStarter.startActivityFromDraft(context, draft as DraftComment)
            draft is DraftForwardPost ->
                PublishForwardStarter.startActivityFromDraft(context, draft as DraftForwardPost)
            draft is DraftReplyBack ->
                PublishReplyBackStarter.startActivityFromDraft(context, draft as DraftReplyBack)
            draft is DraftReply ->
                PublishReplyStarter.startActivityFromDraft(context, draft as DraftReply)
        }
    }
}

object Analytics {
    fun log(draftId: String, mainSource: EventLog1.Publish.MainSource,
            source: EventLog1.Publish.Source, pageType: EventLog1.Publish.PageType) {
        PublisherEventManager.INSTANCE.page_type = pageType.value
        PublishAnalyticsManager.getInstance().generateEventModel(draftId)
                .setMainSource(mainSource).setSource(source).setPageType(pageType)
    }
}