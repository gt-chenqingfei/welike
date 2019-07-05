package com.redefine.welike.business.message.ui.viewholder;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.framework.Event;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.feeds.management.CommentsManager;
import com.redefine.welike.business.feeds.management.SinglePostManager;
import com.redefine.welike.business.feeds.management.bean.Comment;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;
import com.redefine.welike.business.message.management.bean.CommentNotification;
import com.redefine.welike.business.message.management.bean.NotificationBase;
import com.redefine.welike.business.message.management.bean.PostNotification;
import com.redefine.welike.business.message.management.bean.ReplyNotification;
import com.redefine.welike.business.publisher.ui.activity.PublishCommentStarter;
import com.redefine.welike.business.publisher.ui.activity.PublishForwardStarter;
import com.redefine.welike.business.publisher.ui.activity.PublishReplyBackStarter;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.commonui.widget.VipAvatar;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by gongguan on 2018/1/25.
 */

public abstract class BaseMessageContentViewHolder extends BaseRecyclerViewHolder<NotificationBase> {
    public static final String GENERAL_FOLLOW_ACTION = "FOLLOW";
    public static final String MENTION_FORWARD_ACTION = "FORWARD";
    public static final String MENTION_POST_ACTION = "POST_MENTION";
    public static final String MENTION_COMMENT_ACTION = "COMMENT_MENTION";
    public static final String MENTION_REPLY_ACTION = "REPLY_MENTION";
    public static final String COMMENT_COMMENT_ACTION = "COMMENT";
    public static final String COMMENT_REPLY_ACTION = "REPLY";
    public static final String LIKE_POST_ACTION = "POST_LIKE";
    public static final String LIKE_COMMENT_ACTION = "COMMENT_LIKE";
    public static final String LIKE_REPLY_ACTION = "REPLY_LIKE";
    private TextView tv_forwarwed, tv_comment, tv_like;
    private VipAvatar headView;
    private String grayStr;
    protected String name = " ";

    protected BaseMessageContentViewHolder(View itemView, String grayStr) {
        super(itemView);
        this.grayStr = grayStr;

        headView = itemView.findViewById(R.id.simple_message_sys_notify);
        tv_forwarwed = itemView.findViewById(R.id.tv_message_forward);
        tv_comment = itemView.findViewById(R.id.tv_message_comment);
        tv_like = itemView.findViewById(R.id.tv_message_like);

        initViews();
        initEvents();
    }

    private void initEvents() {
        tv_forwarwed.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_forward_text"));
        tv_comment.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_reply_text"));
        tv_like.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_like_text"));

        switch (grayStr) {
            case GENERAL_FOLLOW_ACTION:
                name = ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "app_name");
                grayStr = "";
                break;
            case MENTION_FORWARD_ACTION:
                grayStr = ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_forward_you_text");
                break;
            case MENTION_POST_ACTION:
                grayStr = ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_mentioned_you");
                break;
            case MENTION_COMMENT_ACTION:
                grayStr = ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_commented_you_post_text");
                break;
            case MENTION_REPLY_ACTION:
                grayStr = ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_reply_you_text");
                break;
            case COMMENT_COMMENT_ACTION:
                grayStr = ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_reposted_you_post_text");
                break;
            case COMMENT_REPLY_ACTION:
                grayStr = ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_reply_you_text");
                break;
            case LIKE_POST_ACTION:
                grayStr = ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_liked_your_post_text");
                tv_like.setVisibility(View.GONE);
                tv_comment.setVisibility(View.GONE);
                tv_forwarwed.setVisibility(View.GONE);
                break;
            case LIKE_COMMENT_ACTION:
                grayStr = ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_liked_your_comment_text");
                tv_like.setVisibility(View.GONE);
                tv_comment.setVisibility(View.GONE);
                tv_forwarwed.setVisibility(View.GONE);
                break;
            case LIKE_REPLY_ACTION:
                grayStr = ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_liked_your_reply_text");
                tv_like.setVisibility(View.GONE);
                tv_comment.setVisibility(View.GONE);
                tv_forwarwed.setVisibility(View.GONE);
                break;
        }

        bindEvents(grayStr);
    }

    protected SpannableString insertSpan(SpannableString spanString, int length) {
        spanString.setSpan(new ForegroundColorSpan(Color.GRAY),
                length, grayStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }

    private void onclickListener(final NotificationBase notificationBase) {
        tv_forwarwed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notificationBase instanceof CommentNotification) {
                    PublishForwardStarter.INSTANCE.startActivity4CommentFromNotification(v.getContext(), ((CommentNotification) notificationBase).getPost(), ((CommentNotification) notificationBase).getComment());
                } else if (notificationBase instanceof ReplyNotification) {
                    PublishForwardStarter.INSTANCE.startActivity4CommentFromNotification(v.getContext(), ((ReplyNotification) notificationBase).getPost(), ((ReplyNotification) notificationBase).getRelpy());
                } else if (notificationBase instanceof PostNotification) {
                    PublishForwardStarter.INSTANCE.startActivity4PostFromNotification(v.getContext(), ((PostNotification) notificationBase).getPost());
                }
            }
        });

        tv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notificationBase instanceof CommentNotification) {
                    PublishCommentStarter.INSTANCE.startActivityFromNotification(v.getContext(), ((CommentNotification) notificationBase).getPost());
                } else if (notificationBase instanceof PostNotification) {
                    PublishCommentStarter.INSTANCE.startActivityFromNotification(v.getContext(), ((PostNotification) notificationBase).getPost());
                } else if (notificationBase instanceof ReplyNotification) {
                    PublishReplyBackStarter.INSTANCE.startActivityFromNotification(v.getContext(), ((ReplyNotification) notificationBase).getRelpy(), ((ReplyNotification) notificationBase).getComment());
                }
            }
        });

        tv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLikeBtnClick(notificationBase);
            }
        });

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notificationBase instanceof PostNotification) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(FeedConstant.KEY_FEED_DETAIL_INDEX, FeedConstant.ERROR_INDEX);
                    bundle.putSerializable(FeedConstant.KEY_FEED, ((PostNotification) notificationBase).getPost());
                    EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_FEED_DETAIL_EVENT, bundle));
                } else if (notificationBase instanceof CommentNotification) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(FeedConstant.KEY_COMMENT, ((CommentNotification) notificationBase).getComment());
                    bundle.putSerializable(FeedConstant.KEY_FEED, ((CommentNotification) notificationBase).getPost());
                    EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_COMMENT_DETAIL_EVENT, bundle));
                } else if (notificationBase instanceof ReplyNotification) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(FeedConstant.KEY_FEED_DETAIL_INDEX, FeedConstant.ERROR_INDEX);
                    bundle.putSerializable(FeedConstant.KEY_FEED, ((ReplyNotification) notificationBase).getPost());
                    bundle.putSerializable(FeedConstant.KEY_COMMENT, ((ReplyNotification) notificationBase).getComment());
                    bundle.putSerializable(FeedConstant.KEY_FEED, ((ReplyNotification) notificationBase).getPost());
                    EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_COMMENT_DETAIL_EVENT, bundle));
                }
            }
        });
    }

    private void doLikeBtnClick(NotificationBase notificationBase) {
        if (notificationBase instanceof CommentNotification) {
            Comment comment = ((CommentNotification) notificationBase).getComment();
            if (comment.isLike()) {
                CommentsManager.dislikeComment(comment.getCid());
                tv_like.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_like_text"));
            } else {
                CommentsManager.likeComment(comment.getCid());
                tv_like.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_dislike_text"));
            }

        } else if (notificationBase instanceof PostNotification) {
            PostBase postBase = ((PostNotification) notificationBase).getPost();
            if (postBase.isLike()) {
                SinglePostManager.dislike(postBase.getPid());
                tv_like.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_like_text"));
            } else {
                SinglePostManager.like(postBase.getPid());
                tv_like.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_dislike_text"));
            }
        } else if (notificationBase instanceof ReplyNotification) {
            Comment comment = ((ReplyNotification) notificationBase).getRelpy();
            if (comment.isLike()) {
                CommentsManager.dislikeReply(comment.getCid());
                tv_like.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_like_text"));
            } else {
                CommentsManager.likeReply(comment.getCid());
                tv_like.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_dislike_text"));
            }
        }

    }

    @Override
    public void bindViews(final RecyclerView.Adapter adapter, final NotificationBase feedBase) {
        super.bindViews(adapter, feedBase);

//        HeadUrlLoader.getInstance().loadHeaderUrl(headView, feedBase.getUrl());
        VipUtil.set(headView, feedBase.getUrl(), feedBase.getVip());

        if (feedBase instanceof CommentNotification) {
            if (((CommentNotification) feedBase).getComment().isLike()) {
                tv_like.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_dislike_text"));
            } else {
                tv_like.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_like_text"));
            }
        } else if (feedBase instanceof PostNotification) {
            if (((PostNotification) feedBase).getPost().isLike()) {
                tv_like.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_dislike_text"));
            } else {
                tv_like.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_like_text"));
            }
        } else if (feedBase instanceof ReplyNotification) {
            if (((ReplyNotification) feedBase).getRelpy().isLike()) {
                tv_like.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_dislike_text"));
            } else {
                tv_like.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_like_text"));
            }
        }

        onclickListener(feedBase);
    }

    protected abstract void initViews();

    protected abstract void bindEvents(String spannableString);

}
