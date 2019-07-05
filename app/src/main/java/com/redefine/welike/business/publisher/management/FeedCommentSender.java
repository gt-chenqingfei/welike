package com.redefine.welike.business.publisher.management;

import android.content.Context;
import android.os.Message;
import android.text.Spannable;
import android.text.TextUtils;

import com.redefine.foundation.utils.CommonHelper;
import com.redefine.richtext.RichContent;
import com.redefine.richtext.helper.RichTextHelper;
import com.redefine.richtext.helper.RichTextLoader;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.constant.MessageIdConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.business.feeds.management.bean.Comment;
import com.redefine.welike.business.publisher.management.bean.DraftComment;
import com.redefine.welike.business.publisher.management.draft.PublishCommentDraft;
import com.redefine.welike.business.publisher.management.handler.PublishMessage;
import com.redefine.welike.business.publisher.management.task.IPublishTask;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

/**
 * Created by liubin on 2018/1/15.
 */

public class FeedCommentSender extends AbsFeedPublish<DraftComment> {

    @Override
    public void publish(DraftComment commentDraft) {
        super.publish(commentDraft);
        if (commentDraft == null) return;
        if (commentDraft.isAsRepost()) {
            commentDraft.setFcontent(mergeForwardRichText(MyApplication.getAppContext(), commentDraft));
        }
        commentDraft.setShow(false);
        DraftManager.getInstance().insertOrUpdate(commentDraft);

        PublishManager.Companion.getInstance().publish(new PublishCommentDraft(commentDraft),this);
        Comment comment = buildOfflineComment(commentDraft);
        Message message = Message.obtain();
        message.what = MessageIdConstant.MESSAGE_COMMENT_PUBLISH;
        message.obj = comment;
        EventBus.getDefault().post(message);
    }

    @Override
    public void onPublishCompleted(@NotNull IPublishTask task, @Nullable PublishMessage.PublishState state) {
        super.onPublishCompleted(task, state);
//        PublishManager.Companion.getInstance().unRegisterPublishCallback(this);
    }

    public Comment buildOfflineComment(DraftComment commentDraft) {
        Comment comment = new Comment();

        comment.setCid(CommonHelper.generateUUID());
        comment.setPid(commentDraft.getPid());
        comment.setUid(AccountManager.getInstance().getAccount().getUid());
        comment.setNickName(AccountManager.getInstance().getAccount().getNickName());
        comment.setHead(AccountManager.getInstance().getAccount().getHeadUrl());
        comment.setCurLevel(AccountManager.getInstance().getAccount().getCurLevel());
        comment.setVip(AccountManager.getInstance().getAccount().getVip());
        comment.setFollowing(false);
        comment.setFollower(false);
        comment.setTime(new Date().getTime());
        comment.setContent(commentDraft.getContent().text);
        comment.setLikeCount(0);
        comment.setLike(false);
        comment.setRichItemList(commentDraft.getContent().richItemList);

        return comment;
    }


    private RichContent mergeForwardRichText(Context context, DraftComment commentDraft) {
        if (commentDraft.getFcontent() == null || TextUtils.isEmpty(commentDraft.getFcontent().text)) {
            return commentDraft.getContent();
        }

        String reply = GlobalConfig.REPLY_TEXT;
        RichContent mentionRichContent = RichTextHelper.createRichContent(commentDraft.getCommentUid(), commentDraft.getCommentNick());
        RichContent replyRichContent = RichTextHelper.mergeRichText(reply, mentionRichContent);
        replyRichContent = RichTextHelper.mergeRichText(replyRichContent, " ");
        replyRichContent = RichTextHelper.mergeRichText(replyRichContent, commentDraft.getContent());
        RichTextLoader loader = new RichTextLoader(context);
        Spannable spannable = loader.parseRichContent(replyRichContent.text, replyRichContent.richItemList);

        if (RichTextHelper.getRichTextLength(spannable) > GlobalConfig.PUBLISH_COMMENT_INPUT_TEXT_MAX_OVER_LIMIT) {
            return replyRichContent;
        }

        // 继续拼接post
        RichContent postRich = RichTextHelper.mergeRichText("//", mentionRichContent);
        postRich = RichTextHelper.mergeRichText(postRich, ": ");
        postRich = RichTextHelper.mergeRichText(postRich, commentDraft.getFcontent());
        RichContent replyAndCommentAndPost = RichTextHelper.mergeRichText(replyRichContent, postRich);
        spannable = loader.parseRichContent(replyAndCommentAndPost.text, replyAndCommentAndPost.richItemList);

        if (RichTextHelper.getRichTextLength(spannable) > GlobalConfig.PUBLISH_COMMENT_INPUT_TEXT_MAX_OVER_LIMIT) {
            return replyRichContent;
        } else {
            return replyAndCommentAndPost;
        }
    }
}
