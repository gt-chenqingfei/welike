package com.redefine.welike.business.publisher.management;


import android.os.Message;
import android.text.TextUtils;

import com.redefine.welike.base.constant.MessageIdConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.business.feeds.management.bean.Comment;
import com.redefine.welike.business.publisher.management.bean.DraftBase;
import com.redefine.welike.business.publisher.management.bean.DraftComment;
import com.redefine.welike.business.publisher.management.bean.DraftPost;
import com.redefine.welike.commonui.event.commonenums.BooleanValue;
import com.redefine.welike.commonui.event.model.PublishEventModel;
import com.redefine.welike.hive.AppsFlyerManager;

import org.greenrobot.eventbus.EventBus;

public class BrowsePublishManager {


    private static BrowsePublishManager instance;
    private DraftBase draftBase;

    public static BrowsePublishManager getInstance() {
        if (instance == null)
            instance = new BrowsePublishManager();
        return instance;
    }

    public void setDraftBase(DraftBase draftBase) {
        this.draftBase = draftBase;
    }

    public void doSendComment() {

        if (draftBase == null) {
            return;
        }

        if (draftBase instanceof DraftComment) {

            DraftComment draftComment = (DraftComment) draftBase;
            Account account = AccountManager.getInstance().getAccount();
            if (account != null && !(TextUtils.isEmpty(account.getUid()))) {
                FeedCommentSender commentSender = new FeedCommentSender();
                commentSender.publish(draftComment);

                Comment comment = commentSender.buildOfflineComment(draftComment);
                Message message = Message.obtain();
                message.what = MessageIdConstant.MESSAGE_COMMENT_PUBLISH;
                message.obj = comment;
                EventBus.getDefault().post(message);

                AppsFlyerManager.addEvent(AppsFlyerManager.EVENT_POST);
                PublisherEventManager.INSTANCE.setAlso_repost(draftComment.isAsRepost() ? 1 : 0);
                PublisherEventManager.INSTANCE.report14();

                PublishEventModel model = PublishAnalyticsManager.Companion.getInstance().obtainEventModel(draftBase.getDraftId());
                model.setAlsoRepost(draftComment.isAsRepost() ? BooleanValue.YES : BooleanValue.NO).getProxy().report14();
            }
        } else if (draftBase instanceof DraftPost) {
            DraftPost draftPost = (DraftPost) draftBase;
//            FeedPoster feedPoster = new FeedPoster();
            FeedPoster.Companion.getInstance().publish(draftPost);
        }

        draftBase = null;

    }


}
