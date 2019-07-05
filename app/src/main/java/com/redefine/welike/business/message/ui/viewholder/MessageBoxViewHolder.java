package com.redefine.welike.business.message.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.fresco.loader.MessageCardUrlLoader;
import com.redefine.richtext.RichTextView;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.base.util.TimeUtil;
import com.redefine.welike.business.feeds.management.bean.Comment;
import com.redefine.welike.business.feeds.management.bean.ForwardPost;
import com.redefine.welike.business.feeds.management.bean.LinkPost;
import com.redefine.welike.business.feeds.management.bean.PicPost;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.bean.TextPost;
import com.redefine.welike.business.feeds.management.bean.VideoPost;
import com.redefine.welike.business.feeds.ui.page.CommentDetailActivity;
import com.redefine.welike.business.feeds.ui.page.FeedDetailActivity;
import com.redefine.welike.business.message.management.bean.CommentNotification;
import com.redefine.welike.business.message.management.bean.NotificationBase;
import com.redefine.welike.business.message.management.bean.PostNotification;
import com.redefine.welike.business.message.management.bean.ReplyNotification;
import com.redefine.welike.business.user.ui.page.UserHostPage;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.common.util.DateTimeUtil;
import com.redefine.welike.commonui.widget.VipAvatar;

/**
 * Created by liwenbo on 2018/3/10.
 */

public class MessageBoxViewHolder extends BaseMessageViewHolder {
    private final VipAvatar mHeadView;
    private final TextView mNickName;
    private final TextView mRemindText;
    private final TextView mTime;
    private final RichTextView mMessageText;
    private final SimpleDraweeView mMessageCardImg;
    private final View mPlayBtn;
    private final RichTextView mMessageCardText;
    private final View mCardView;
    private final View mMessageCardImgLayout;

    public MessageBoxViewHolder(View itemView) {
        super(itemView);
        mHeadView = itemView.findViewById(R.id.message_head);
        mNickName = itemView.findViewById(R.id.message_nick_name);
        mRemindText = itemView.findViewById(R.id.message_remind_text);
        mCardView = itemView.findViewById(R.id.message_card_layout);
        mTime = itemView.findViewById(R.id.message_nick_time);

        mMessageText = itemView.findViewById(R.id.message_text);
        mMessageText.setMovementMethod(null);
        mMessageCardImg = itemView.findViewById(R.id.message_card_img);
        mMessageCardImgLayout = itemView.findViewById(R.id.message_card_img_layout);

        mPlayBtn = itemView.findViewById(R.id.message_card_play);
        mMessageCardText = itemView.findViewById(R.id.message_card_text);
        mMessageCardText.setMovementMethod(null);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, final NotificationBase feedBase) {
        super.bindViews(adapter, feedBase);
//        HeadUrlLoader.getInstance().loadHeaderUrl(mHeadView, feedBase.getSourceHead());
        VipUtil.set(mHeadView,feedBase.getSourceHead(),feedBase.getVip());
        mNickName.setText(feedBase.getSourceNickName());
        mTime.setText(DateTimeUtil.INSTANCE.getMessageReceiveTime(mTime.getResources(), feedBase.getTime()));
        mNickName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(feedBase.getSourceUid())) {
                    UserHostPage.launch(true, feedBase.getSourceUid());
                }
            }
        });
        mHeadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(feedBase.getSourceUid())) {
                    UserHostPage.launch(true, feedBase.getSourceUid());
                }
            }
        });
        initRemindText(feedBase);

        if (feedBase instanceof CommentNotification) {
            parseCommentNotification(((CommentNotification) feedBase), ((CommentNotification) feedBase).getComment());
        } else if (feedBase instanceof PostNotification) {
            parsePostNotification(((PostNotification) feedBase));
        } else if (feedBase instanceof ReplyNotification) {
            parseReplyNotification(((ReplyNotification) feedBase), ((ReplyNotification) feedBase).getComment(), ((ReplyNotification) feedBase).getRelpy());
        }
    }

    private void parseReplyNotification(final ReplyNotification feedBase, final Comment comment, Comment relpy) {
        mMessageText.setVisibility(View.VISIBLE);
        mMessageText.getRichProcessor().setRichContent(relpy.getContent(), relpy.getRichItemList());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentDetailActivity.launch(feedBase.getPost(), comment);
            }
        });
        PostBase postBase = feedBase.getPost();
        if (postBase instanceof ForwardPost) {
            mMessageCardImgLayout.setVisibility(View.GONE);
            mMessageCardText.getRichProcessor().setRichContent(postBase.getSummary(), postBase.getRichItemList());
        } else {
            parsePost(feedBase.getPost());
        }
    }

    private void parseCommentNotification(final CommentNotification feedBase, Comment comment) {
        mMessageText.setVisibility(View.VISIBLE);
        mMessageText.getRichProcessor().setRichContent(comment.getContent(), comment.getRichItemList());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedDetailActivity.launch(feedBase.getPost());
            }
        });
        PostBase postBase = feedBase.getPost();
        if (postBase instanceof ForwardPost) {
            mMessageCardImgLayout.setVisibility(View.GONE);
            mMessageCardText.getRichProcessor().setRichContent(postBase.getSummary(), postBase.getRichItemList());
        } else {
            parsePost(feedBase.getPost());
        }
    }

    private void parsePostNotification(final PostNotification feedBase) {
        final PostBase postBase = feedBase.getPost();
        if (postBase instanceof ForwardPost) {
            mMessageText.setVisibility(View.VISIBLE);
            mMessageText.getRichProcessor().setRichContent(postBase.getSummary(), postBase.getRichItemList());
            if (((ForwardPost) postBase).isForwardDeleted()) {
                mMessageCardImgLayout.setVisibility(View.VISIBLE);
                mPlayBtn.setVisibility(View.GONE);
                mMessageCardImg.setImageResource(R.drawable.message_error_icon);
                mMessageCardText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "forward_feed_delete_content"));
            } else {
                parsePost(((ForwardPost) postBase).getRootPost());
                mCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FeedDetailActivity.launch(((ForwardPost) postBase).getRootPost());
                    }
                });
            }
        } else {
            mMessageText.setVisibility(View.GONE);
            parsePost(postBase);
        }
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedDetailActivity.launch(feedBase.getPost());
            }
        });
    }

    private void parsePost(final PostBase postBase) {
        mPlayBtn.setVisibility(View.GONE);
        if (postBase instanceof PicPost) {
            if (((PicPost) postBase).picCount() == 0) {
                mMessageCardImgLayout.setVisibility(View.GONE);
            } else {
                mMessageCardImgLayout.setVisibility(View.VISIBLE);
                MessageCardUrlLoader.getInstance().loadMessageCardUrl(mMessageCardImg, ((PicPost) postBase).getPicInfo(0).getThumbUrl());
            }
        } else if (postBase instanceof TextPost) {
            mMessageCardImgLayout.setVisibility(View.GONE);
        } else if (postBase instanceof VideoPost) {

            if (TextUtils.isEmpty(((VideoPost) postBase).getCoverUrl())) {
                mMessageCardImgLayout.setVisibility(View.GONE);
            } else {
                mPlayBtn.setVisibility(View.VISIBLE);
                mMessageCardImgLayout.setVisibility(View.VISIBLE);
                MessageCardUrlLoader.getInstance().loadMessageCardUrl(mMessageCardImg, ((VideoPost) postBase).getCoverUrl());
            }
        } else if (postBase instanceof LinkPost) {

            if (TextUtils.isEmpty(((LinkPost) postBase).getLinkThumbUrl())) {
                mMessageCardImgLayout.setVisibility(View.GONE);
            } else {
                mMessageCardImgLayout.setVisibility(View.VISIBLE);
                MessageCardUrlLoader.getInstance().loadMessageCardUrl(mMessageCardImg, ((LinkPost) postBase).getLinkThumbUrl());
            }
        }
        mMessageCardText.getRichProcessor().setRichContent(postBase.getSummary(), postBase.getRichItemList());
        mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedDetailActivity.launch(postBase);
            }
        });
    }

    private void initRemindText(NotificationBase feedBase) {
        if (TextUtils.equals(feedBase.getAction(), NotificationBase.MENTION_FORWARD_ACTION)) {
//            if (!TextUtils.equals(feedBase.getTargetUid(), AccountManager.getInstance().getAccount().getUid())) {
//                mRemindText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.IM,"message_comment_reposted_you_post_text"));
//            } else {
            mRemindText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_mentioned_you"));
//            }
        } else if (TextUtils.equals(feedBase.getAction(), NotificationBase.MENTION_POST_ACTION)) {
            mRemindText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_mentioned_you"));
        } else if (TextUtils.equals(feedBase.getAction(), NotificationBase.MENTION_COMMENT_ACTION)) {
//            if (!TextUtils.equals(feedBase.getTargetUid(), AccountManager.getInstance().getAccount().getUid())) {
//                mRemindText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.IM,"message_comment_commented_you_post_text"));
//            } else {
            mRemindText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_mentioned_you"));
//            }
        } else if (TextUtils.equals(feedBase.getAction(), NotificationBase.MENTION_REPLY_ACTION)) {
//            if (!TextUtils.equals(feedBase.getTargetUid(), AccountManager.getInstance().getAccount().getUid())) {
//                mRemindText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.IM,"message_comment_reply_you_text"));
//            } else {
            mRemindText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_mentioned_you"));
//            }
        } else if (TextUtils.equals(feedBase.getAction(), NotificationBase.COMMENT_COMMENT_ACTION)) {
            mRemindText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_commented_you_post_text"));
        } else if (TextUtils.equals(feedBase.getAction(), NotificationBase.COMMENT_REPLY_ACTION)) {
            mRemindText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_reply_you_text"));
        } else if (TextUtils.equals(feedBase.getAction(), NotificationBase.LIKE_POST_ACTION)) {
            mRemindText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_liked_your_post_text"));
        } else if (TextUtils.equals(feedBase.getAction(), NotificationBase.LIKE_COMMENT_ACTION)) {
            mRemindText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_liked_your_comment_text"));
        } else if (TextUtils.equals(feedBase.getAction(), NotificationBase.LIKE_REPLY_ACTION)) {
            mRemindText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "message_comment_liked_your_reply_text"));
        }
        String remindTextStr = mRemindText.getText().toString();
        if (!TextUtils.isEmpty(remindTextStr)) {

            int remindStrLength = (int) mRemindText.getPaint().measureText(remindTextStr);

            int nickNameStrLength = (int) mNickName.getPaint().measureText(feedBase.getSourceNickName());
//
//            int w = View.MeasureSpec.makeMeasureSpec(0,
//                    View.MeasureSpec.UNSPECIFIED);
//            int h = View.MeasureSpec.makeMeasureSpec(0,
//                    View.MeasureSpec.UNSPECIFIED);
//            LinearLayout parent = (LinearLayout) mRemindText.getParent();
//            parent.measure(w, h);
//            int length = parent.getMeasuredWidth() - remindStrLength;
//            length = length < nickNameStrLength ? length : nickNameStrLength;
            if (remindStrLength + nickNameStrLength > magic) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mNickName.getLayoutParams();
                params.width = magic-remindStrLength;
                mNickName.setLayoutParams(params);
            }
        }
    }

    private int magic = 0;

    public void setMagic(int magic) {
        this.magic = magic*9/10;
    }
}
