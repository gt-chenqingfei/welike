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
import com.redefine.welike.business.publisher.management.bean.DraftReply;
import com.redefine.welike.business.publisher.management.draft.PublishReplyDraft;
import com.redefine.welike.business.publisher.management.handler.PublishMessage;
import com.redefine.welike.business.publisher.management.task.IPublishTask;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by liubin on 2018/1/15.
 */

public class FeedCommentReplier extends AbsFeedPublish<DraftReply> {

    @Override
    public void publish(DraftReply replyDraft) {
        super.publish(replyDraft);
        if (replyDraft == null) return ;

        if (replyDraft.isAsRepost()) {
            // 拼装转发post
            replyDraft.setCcontent(mergeForwardRichText(MyApplication.getAppContext(), replyDraft));
        }

        replyDraft.setShow(false);
        DraftManager.getInstance().insertOrUpdate(replyDraft);
        PublishManager.Companion.getInstance().publish(new PublishReplyDraft(replyDraft),this);

        Comment comment = buildOfflineComment(replyDraft);
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

    public Comment buildOfflineComment(DraftReply replyDraft) {
        Comment child = new Comment();

        child.setCid(CommonHelper.generateUUID());
        child.setPid(replyDraft.getPid());
        child.setUid(AccountManager.getInstance().getAccount().getUid());
        child.setNickName(AccountManager.getInstance().getAccount().getNickName());
        child.setHead(AccountManager.getInstance().getAccount().getHeadUrl());
        child.setCurLevel(AccountManager.getInstance().getAccount().getCurLevel());
        child.setVip(AccountManager.getInstance().getAccount().getVip());
        child.setFollowing(false);
        child.setFollower(false);
        child.setTime(new Date().getTime());
        child.setContent(replyDraft.getContent().text);
        child.setLikeCount(0);
        child.setLike(false);
        child.setRichItemList(replyDraft.getContent().richItemList);

        List<Comment> children = new ArrayList<>();
        children.add(child);

        Comment comment = new Comment();
        comment.setCid(replyDraft.getCid());
        comment.setPid(replyDraft.getPid());
        comment.setChildren(children);

        return comment;
    }


    /**
     * 拼接reply的转发文案
     *
     * @param replyDraft
     * @return
     */
    public static RichContent mergeForwardRichText(Context context, DraftReply replyDraft) {

        String reply = GlobalConfig.REPLY_TEXT;
        RichContent mentionRichContent = RichTextHelper.createRichContent(replyDraft.getCommentUid(), replyDraft.getCommentNick());
        RichContent replyRichContent = RichTextHelper.mergeRichText(reply, mentionRichContent);
        replyRichContent = RichTextHelper.mergeRichText(replyRichContent, " ");
        replyRichContent = RichTextHelper.mergeRichText(replyRichContent, replyDraft.getContent());
        RichTextLoader loader = new RichTextLoader(context);
        Spannable spannable = loader.parseRichContent(replyRichContent.text, replyRichContent.richItemList);

        if (RichTextHelper.getRichTextLength(spannable) > GlobalConfig.PUBLISH_COMMENT_INPUT_TEXT_MAX_OVER_LIMIT) {
            return replyRichContent;
        }


        if (replyDraft.getCcontent() == null || TextUtils.isEmpty(replyDraft.getCcontent().text)) {
            return replyRichContent;
        }

        // 继续拼接comment
        RichContent commentRich = RichTextHelper.mergeRichText("//", mentionRichContent);
        commentRich = RichTextHelper.mergeRichText(commentRich, ": ");
        commentRich = RichTextHelper.mergeRichText(commentRich, replyDraft.getCcontent());

        RichContent replyAndComment = RichTextHelper.mergeRichText(replyRichContent, commentRich);

        spannable = loader.parseRichContent(replyAndComment.text, replyAndComment.richItemList);

        if (RichTextHelper.getRichTextLength(spannable) > GlobalConfig.PUBLISH_COMMENT_INPUT_TEXT_MAX_OVER_LIMIT) {
            return replyRichContent;
        } else {
            return replyAndComment;
        }
    }


}
