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
import com.redefine.welike.business.publisher.management.bean.DraftReplyBack;
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
 * Created by liubin on 2018/1/21.
 */

public class FeedReplyReplier extends AbsFeedPublish<DraftReplyBack> {

    @Override
    public void publish(DraftReplyBack replyReplyDraft) {
        super.publish(replyReplyDraft);
        if (replyReplyDraft == null) return ;
        if (replyReplyDraft.getContent() != null) {
            replyReplyDraft.setContent(mergeReplyReplyRichText(replyReplyDraft));

            if (replyReplyDraft.isAsRepost()) {
                // 拼装转发post
                replyReplyDraft.setRcontent(mergeForwardRichText(MyApplication.getAppContext(), replyReplyDraft));
            }
        }
        replyReplyDraft.setShow(false);
        DraftManager.getInstance().insertOrUpdate(replyReplyDraft);
        PublishManager.Companion.getInstance().publish(new PublishReplyDraft(replyReplyDraft),this);

        Comment comment = buildOfflineComment(replyReplyDraft);
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

    public Comment buildOfflineComment(DraftReplyBack replyReplyDraft) {
        Comment child = new Comment();

        child.setCid(CommonHelper.generateUUID());
        child.setPid(replyReplyDraft.getPid());
        child.setUid(AccountManager.getInstance().getAccount().getUid());
        child.setNickName(AccountManager.getInstance().getAccount().getNickName());
        child.setHead(AccountManager.getInstance().getAccount().getHeadUrl());
        child.setFollowing(false);
        child.setFollower(false);
        child.setCurLevel(AccountManager.getInstance().getAccount().getCurLevel());
        child.setVip(AccountManager.getInstance().getAccount().getVip());
        child.setTime(new Date().getTime());
        RichContent richContent = replyReplyDraft.getContent();
        child.setContent(richContent.text);
        child.setLikeCount(0);
        child.setLike(false);
        child.setRichItemList(richContent.richItemList);

        List<Comment> children = new ArrayList<>();
        children.add(child);

        Comment comment = new Comment();
        comment.setCid(replyReplyDraft.getCid());
        comment.setPid(replyReplyDraft.getPid());
        comment.setChildren(children);

        return comment;
    }

    public static RichContent mergeReplyReplyRichText(DraftReplyBack replyReplyDraft) {
        String reply = GlobalConfig.REPLY_TEXT;
        RichContent mentionRichContent = RichTextHelper.createRichContent(replyReplyDraft.getCommentUid(), replyReplyDraft.getCommentNick());
        RichContent replyRichContent = RichTextHelper.mergeRichText(reply, mentionRichContent);
        replyRichContent = RichTextHelper.mergeRichText(replyRichContent, " ");
        replyRichContent = RichTextHelper.mergeRichText(replyRichContent, replyReplyDraft.getContent());
        return replyRichContent;
    }


    private RichContent mergeForwardRichText(Context context, DraftReplyBack replyReplyDraft) {
        String reply = GlobalConfig.REPLY_TEXT;
        RichContent mentionRichContent = RichTextHelper.createRichContent(replyReplyDraft.getCommentUid(), replyReplyDraft.getCommentNick());
        RichContent replyRichContent = RichTextHelper.mergeRichText(reply, mentionRichContent);
        replyRichContent = RichTextHelper.mergeRichText(replyRichContent, " ");
        replyRichContent = RichTextHelper.mergeRichText(replyRichContent, replyReplyDraft.getContent());
        RichTextLoader loader = new RichTextLoader(context);
        Spannable spannable = loader.parseRichContent(replyRichContent.text, replyRichContent.richItemList);

        if (RichTextHelper.getRichTextLength(spannable) > GlobalConfig.PUBLISH_COMMENT_INPUT_TEXT_MAX_OVER_LIMIT) {
            return replyRichContent;
        }

        if (replyReplyDraft.getRcontent() == null || TextUtils.isEmpty(replyReplyDraft.getRcontent().text)) {
            return replyRichContent;
        }

        // 继续拼接reply
        RichContent replyRich = RichTextHelper.mergeRichText("//", mentionRichContent);
        replyRich = RichTextHelper.mergeRichText(replyRich, ": ");
        replyRich = RichTextHelper.mergeRichText(replyRich, replyReplyDraft.getRcontent());

        RichContent replyReplyAndReply = RichTextHelper.mergeRichText(replyRichContent, replyRich);

        spannable = loader.parseRichContent(replyReplyAndReply.text, replyReplyAndReply.richItemList);

        if (RichTextHelper.getRichTextLength(spannable) > GlobalConfig.PUBLISH_COMMENT_INPUT_TEXT_MAX_OVER_LIMIT) {
            return replyRichContent;
        } else {
            return replyReplyAndReply;
        }
    }

}
