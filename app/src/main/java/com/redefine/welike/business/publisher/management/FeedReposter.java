package com.redefine.welike.business.publisher.management;

import android.os.Message;
import android.text.TextUtils;

import com.redefine.foundation.utils.CommonHelper;
import com.redefine.welike.base.constant.MessageIdConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.business.feeds.management.bean.ForwardPost;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.publisher.management.bean.DraftForwardPost;
import com.redefine.welike.business.publisher.management.draft.PublishForwardDraft;
import com.redefine.welike.business.publisher.management.handler.PublishMessage;
import com.redefine.welike.business.publisher.management.task.IPublishTask;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by liubin on 2018/1/15.
 */

public class FeedReposter extends AbsFeedPublish<DraftForwardPost> {

    @Override
    public void publish(DraftForwardPost forwardPostDraft) {
        super.publish(forwardPostDraft);
        if (forwardPostDraft == null) return ;
        if (forwardPostDraft.getRootPost() == null) return ;
        String pid = null;
        if (forwardPostDraft.getRootPost() != null) {
            pid = forwardPostDraft.getRootPost().getPid();
        }

        if (TextUtils.isEmpty(forwardPostDraft.getCommentPid())) {
            forwardPostDraft.setCommentPid(pid);
        }
        filedCheck(forwardPostDraft);

        forwardPostDraft.setShow(false);
        DraftManager.getInstance().insertOrUpdate(forwardPostDraft);
        PublishManager.Companion.getInstance().publish(new PublishForwardDraft(forwardPostDraft),this);

        PostBase postBase = buildOfflineForwardPost(forwardPostDraft, forwardPostDraft.getRootPost());
        Message message = Message.obtain();
        message.what = MessageIdConstant.MESSAGE_FORWARD_POST_PUBLISH;
        message.obj = postBase;
        EventBus.getDefault().post(message);
    }

    @Override
    public void onPublishCompleted(@NotNull IPublishTask task, @Nullable PublishMessage.PublishState state) {
        super.onPublishCompleted(task, state);
//        PublishManager.Companion.getInstance().unRegisterPublishCallback(this);
    }

    public ForwardPost buildOfflineForwardPost(DraftForwardPost forwardPostDraft, PostBase rootPost) {
        ForwardPost forwardPost = new ForwardPost();
        forwardPost.setPid(CommonHelper.generateUUID());
        forwardPost.setUid(AccountManager.getInstance().getAccount().getUid());
        forwardPost.setType(PostBase.POST_TYPE_FORWARD);
        forwardPost.setTime(new Date().getTime());
        forwardPost.setHeadUrl(AccountManager.getInstance().getAccount().getHeadUrl());
        forwardPost.setNickName(AccountManager.getInstance().getAccount().getNickName());
        forwardPost.setCurLevel(AccountManager.getInstance().getAccount().getCurLevel());
        forwardPost.setVip(AccountManager.getInstance().getAccount().getVip());
        forwardPost.setFollowing(false);
        forwardPost.setFrom(CommonHelper.getDeviceModel());
        forwardPost.setText(forwardPostDraft.getContent().text);
        forwardPost.setSummary(forwardPostDraft.getContent().summary);
        forwardPost.setLike(false);
        forwardPost.setLikeCount(0);
        forwardPost.setCommentCount(0);
        forwardPost.setForwardCount(0);
        forwardPost.setRichItemList(forwardPostDraft.getContent().richItemList);
        forwardPost.setRootPost(rootPost);
        forwardPost.setForwardDeleted(forwardPostDraft.isForwardDeleted());

        return forwardPost;
    }


    private void filedCheck(DraftForwardPost forwardPostDraft) {
        try {
            JSONObject postJson = new JSONObject(forwardPostDraft.getCommentPid());
            JSONObject forwardPostJson = new JSONObject(forwardPostDraft.getRootPost().getPid());

            String post = postJson.optString("id");
            String forwardPost = forwardPostJson.optString("id");

            if (!TextUtils.isEmpty(post)) {
                forwardPostDraft.setCommentPid(post);
            }
            if (!TextUtils.isEmpty(forwardPost)) {
                forwardPostDraft.getRootPost().setPid(forwardPost);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
