package com.redefine.welike.business.message.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.fresco.loader.MessageCardUrlLoader;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.richtext.RichTextView;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.base.util.TimeUtil;
import com.redefine.welike.business.common.LikeManager;
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

public class MessageBoxViewHolder3 extends BaseRecyclerViewHolder<NotificationBase> {
    private final VipAvatar mHeadView;
    private final TextView mNickName;
    private final TextView mRemindText, mRemindText2, mSummary;
    private final TextView mTime;
    private final RichTextView mMessageText;
    private final SimpleDraweeView mMessageCardImg;
    private final View mPlayBtn;
    private final ImageView ivLike;

    //    private final RichTextView mMessageCardText;
//    private final View mCardView;
//    private final View mMessageCardImgLayout;

    public MessageBoxViewHolder3(View itemView) {
        super(itemView);
        mHeadView = itemView.findViewById(R.id.message_head);
        mNickName = itemView.findViewById(R.id.message_nick_name);
        mRemindText = itemView.findViewById(R.id.message_remind_text);
        mRemindText2 = itemView.findViewById(R.id.message_remind_text2);
//        mCardView = itemView.findViewById(R.id.message_card_layout);
        mSummary = itemView.findViewById(R.id.message_card_summary);
        mTime = itemView.findViewById(R.id.message_nick_time);

        mMessageText = itemView.findViewById(R.id.message_text);
        mMessageText.setMovementMethod(null);
        mMessageCardImg = itemView.findViewById(R.id.message_card_img);
//        mMessageCardImgLayout = itemView.findViewById(R.id.message_card_img_layout);
        ivLike = itemView.findViewById(R.id.iv_heart);
        mPlayBtn = itemView.findViewById(R.id.message_card_play);
//        mMessageCardText = itemView.findViewById(R.id.message_card_text);
//        mMessageCardText.setMovementMethod(null);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, final NotificationBase feedBase) {
        super.bindViews(adapter, feedBase);
//        HeadUrlLoader.getInstance().loadHeaderUrl(mHeadView, feedBase.getSourceHead());
        VipUtil.set(mHeadView, feedBase.getSourceHead(), feedBase.getVip());
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

        if (!showLike(feedBase)) {
            if (feedBase instanceof CommentNotification) {
                showComment(((CommentNotification) feedBase));
            } else if (feedBase instanceof PostNotification) {
                showPost(((PostNotification) feedBase));
            } else if (feedBase instanceof ReplyNotification) {
                parseReplyNotification(((ReplyNotification) feedBase));
            }
        }
    }

    //筛选当like 时候。
    private boolean showLike(final NotificationBase base) {
        boolean flag = false;
        if (TextUtils.equals(base.getAction(), NotificationBase.LIKE_COMMENT_ACTION)) {
            if (base instanceof CommentNotification) {
                final CommentNotification o = (CommentNotification) base;
                renderComment(o.getPost(), o.getComment());
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FeedDetailActivity.launch(o.getPost());
                    }
                });
            }
            flag = true;
        }
        if (TextUtils.equals(base.getAction(), NotificationBase.LIKE_POST_ACTION)) {
            if (base instanceof PostNotification) {
                final PostNotification o = (PostNotification) base;
                renderPost(o.getPost());
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FeedDetailActivity.launch(o.getPost());
                    }
                });
            }
            flag = true;
        }
        if (TextUtils.equals(base.getAction(), NotificationBase.LIKE_REPLY_ACTION)) {
            if (base instanceof ReplyNotification) {
                final ReplyNotification o = (ReplyNotification) base;
                renderComment(o.getPost(), o.getRelpy());
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommentDetailActivity.launch(o.getPost(), o.getComment());
                    }
                });
            }
            flag = true;
        }
        if (flag) {
            ivLike.setVisibility(View.VISIBLE);
            mMessageText.setVisibility(View.INVISIBLE);
            if (base.exp <= 0) base.exp = 1;
            ivLike.setImageResource(LikeManager.getImage(base.exp));
        }


        return flag;
    }

    /**
     * comment @
     * comment My Post
     */
    private void showComment(final CommentNotification feedBase) {
        Comment comment = feedBase.getComment();
        mMessageText.setVisibility(View.VISIBLE);
        mMessageText.getRichProcessor().setRichContent(comment.getContent(), comment.getRichItemList());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedDetailActivity.launch(feedBase.getPost());
            }
        });
        if (TextUtils.equals(feedBase.getAction(), NotificationBase.MENTION_COMMENT_ACTION)) {
            renderComment(feedBase.getPost(), feedBase.getComment());
        } else if (TextUtils.equals(feedBase.getAction(), NotificationBase.COMMENT_COMMENT_ACTION)) {
            renderPost(feedBase.getPost());
        }
    }

    /**
     * post @
     * forward post
     */
    private void showPost(final PostNotification feedBase) {
        final PostBase postBase = feedBase.getPost();
        mMessageText.setVisibility(View.VISIBLE);
        mMessageText.getRichProcessor().setRichContent(postBase.getSummary(), postBase.getRichItemList());
        if (postBase instanceof ForwardPost) {
            renderPost(((ForwardPost) postBase).getRootPost());
        } else {
            renderPost(postBase);
        }
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedDetailActivity.launch(feedBase.getPost());
            }
        });
    }

    /**
     * reply @
     * reply my comment.
     */
    private void parseReplyNotification(final ReplyNotification feedBase) {
        final Comment comment = feedBase.getComment();
        Comment reply = feedBase.getRelpy();

        mMessageText.setVisibility(View.VISIBLE);
        mMessageText.getRichProcessor().setRichContent(reply.getContent(), reply.getRichItemList());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentDetailActivity.launch(feedBase.getPost(), comment);
            }
        });
        renderComment(feedBase.getPost(), comment);
//        PostBase postBase = feedBase.getPost();
//        if (postBase instanceof ForwardPost) {
////            mMessageCardImgLayout.setVisibility(View.GONE);
////            mMessageCardText.getRichProcessor().setRichContent(postBase.getSummary(), postBase.getRichItemList());
//        } else {
//            renderReply(reply.getChildren().get(0));
//        }
    }

    private void renderComment(final PostBase post, final Comment comment) {
        mPlayBtn.setVisibility(View.GONE);
        mSummary.setText(comment.getContent());
        mSummary.setVisibility(View.VISIBLE);
        mMessageCardImg.setVisibility(View.INVISIBLE);
        mMessageCardImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentDetailActivity.launch(post, comment);
            }
        });
    }

    private void renderPost(final PostBase postBase) {
        mPlayBtn.setVisibility(View.GONE);
        mSummary.setVisibility(View.VISIBLE);
        mMessageCardImg.setVisibility(View.INVISIBLE);

        if (postBase == null) {
            mPlayBtn.setVisibility(View.GONE);
            mSummary.setVisibility(View.GONE);
            mMessageCardImg.setVisibility(View.VISIBLE);
            mMessageCardImg.setImageResource(R.drawable.message_error_icon);
            return;
        }
        mSummary.setText(postBase.getSummary());
        if (postBase instanceof PicPost) {
            if (((PicPost) postBase).picCount() > 0) {
                mSummary.setVisibility(View.GONE);
                mMessageCardImg.setVisibility(View.VISIBLE);
                MessageCardUrlLoader.getInstance().loadMessageCardUrl(mMessageCardImg, ((PicPost) postBase).getPicInfo(0).getThumbUrl());
            }
        } else if (postBase instanceof TextPost) {
//            mMessageCardImgLayout.setVisibility(View.GONE);
        } else if (postBase instanceof VideoPost) {

            if (!TextUtils.isEmpty(((VideoPost) postBase).getCoverUrl())) {
                mPlayBtn.setVisibility(View.VISIBLE);
                mSummary.setVisibility(View.GONE);
                mMessageCardImg.setVisibility(View.VISIBLE);
                MessageCardUrlLoader.getInstance().loadMessageCardUrl(mMessageCardImg, ((VideoPost) postBase).getCoverUrl());
            }
        } else if (postBase instanceof LinkPost) {

            if (!TextUtils.isEmpty(((LinkPost) postBase).getLinkThumbUrl())) {
                mSummary.setVisibility(View.GONE);
                mMessageCardImg.setVisibility(View.VISIBLE);
                MessageCardUrlLoader.getInstance().loadMessageCardUrl(mMessageCardImg, ((LinkPost) postBase).getLinkThumbUrl());
            }
        }
        mMessageCardImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedDetailActivity.launch(postBase);
            }
        });
        mSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedDetailActivity.launch(postBase);
            }
        });
    }


    private String getRemind(NotificationBase feedBase) {
        // check word
        String key = "message_comment_mentioned_you";
        if (TextUtils.equals(feedBase.getAction(), NotificationBase.MENTION_FORWARD_ACTION)) {
            key = "message_comment_forward_text";
        } else if (TextUtils.equals(feedBase.getAction(), NotificationBase.MENTION_POST_ACTION)) {
            key = "message_comment_mentioned_you";
        } else if (TextUtils.equals(feedBase.getAction(), NotificationBase.MENTION_COMMENT_ACTION)) {
            key = "message_comment_mentioned_you";
        } else if (TextUtils.equals(feedBase.getAction(), NotificationBase.MENTION_REPLY_ACTION)) {
            key = "message_comment_mentioned_you";
        } else if (TextUtils.equals(feedBase.getAction(), NotificationBase.COMMENT_COMMENT_ACTION)) {
            key = "message_comment_commented_you_post_text";
        } else if (TextUtils.equals(feedBase.getAction(), NotificationBase.COMMENT_REPLY_ACTION)) {
            key = "message_comment_reply_you_text";
        } else if (TextUtils.equals(feedBase.getAction(), NotificationBase.LIKE_POST_ACTION)) {
            key = "message_comment_liked_your_post_text";
        } else if (TextUtils.equals(feedBase.getAction(), NotificationBase.LIKE_COMMENT_ACTION)) {
            key = "message_comment_liked_your_comment_text";
        } else if (TextUtils.equals(feedBase.getAction(), NotificationBase.LIKE_REPLY_ACTION)) {
            key = "message_comment_liked_your_reply_text";
        }
        return ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, key);
    }

    private void initRemindText(NotificationBase feedBase) {
        String word = getRemind(feedBase);
        mRemindText.setText(word);
        mRemindText2.setText(word);
        if (!TextUtils.isEmpty(word)) {
            int remindStrLength = (int) mRemindText.getPaint().measureText(word);
            int nickNameStrLength = (int) mNickName.getPaint().measureText(feedBase.getSourceNickName());
            if (remindStrLength + nickNameStrLength > magic) {
                mRemindText.setVisibility(View.GONE);
                mRemindText2.setVisibility(View.VISIBLE);
            } else {
                mRemindText.setVisibility(View.VISIBLE);
                mRemindText2.setVisibility(View.GONE);
            }
        }
    }

    private int magic = 0;

    public void setMagic(int magic) {
        this.magic = magic * 9 / 10;
    }
}
