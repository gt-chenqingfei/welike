package com.redefine.welike.business.message.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.richtext.RichTextView;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.base.util.TimeUtil;
import com.redefine.welike.business.feeds.management.bean.ForwardPost;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.page.FeedDetailActivity;
import com.redefine.welike.business.message.management.bean.KMMagic;
import com.redefine.welike.business.message.management.bean.KMNotify;
import com.redefine.welike.business.message.management.bean.PostNotification;
import com.redefine.welike.business.user.ui.page.UserHostPage;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.common.util.DateTimeUtil;
import com.redefine.welike.commonui.widget.VipAvatar;

/**
 * Created by liwenbo on 2018/3/10.
 */

public class MessageBoxViewHolder2 extends BaseRecyclerViewHolder<KMNotify> {

    public static final String POST_AT = "POST_MENTION";
    public static final String COMMENT_AT = "COMMENT_MENTION";
    public static final String REPLY_AT = "REPLY_MENTION";

    public static final String FORWARD_POST = "FORWARD_POST";
    public static final String FORWARD_COMMENT = "FORWARD_COMMENT";

    public static final String COMMENT = "COMMENT";
    public static final String REPLY = "REPLY";

    public static final String LIKE_POST = "POST_LIKE";
    public static final String LIKE_COMMENT = "COMMENT_LIKE";
    public static final String LIKE_REPLY = "REPLY_LIKE";

    private final VipAvatar mHeadView;
    private final TextView mNickName;
    private final TextView mRemindText, mRemindText2,mSummary;
    private final TextView mTime;
    private final RichTextView mMessageText;
    private final SimpleDraweeView mMessageCardImg;
    private final View mPlayBtn;
//    private final RichTextView mMessageCardText;
    private final View mCardView;
//    private final View mMessageCardImgLayout;

    public MessageBoxViewHolder2(View itemView) {
        super(itemView);
        mHeadView = itemView.findViewById(R.id.message_head);
        mNickName = itemView.findViewById(R.id.message_nick_name);
        mRemindText = itemView.findViewById(R.id.message_remind_text);
        mRemindText2 = itemView.findViewById(R.id.message_remind_text2);
        mCardView = itemView.findViewById(R.id.message_card_layout);
        mSummary = itemView.findViewById(R.id.message_card_summary);
        mTime = itemView.findViewById(R.id.message_nick_time);

        mMessageText = itemView.findViewById(R.id.message_text);
        mMessageText.setMovementMethod(null);
        mMessageCardImg = itemView.findViewById(R.id.message_card_img);
//        mMessageCardImgLayout = itemView.findViewById(R.id.message_card_img_layout);

        mPlayBtn = itemView.findViewById(R.id.message_card_play);
//        mMessageCardText = itemView.findViewById(R.id.message_card_text);
//        mMessageCardText.setMovementMethod(null);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, final KMNotify feedBase) {
        super.bindViews(adapter, feedBase);
//        HeadUrlLoader.getInstance().loadHeaderUrl(mHeadView, feedBase.getSourceHead());
        VipUtil.set(mHeadView, feedBase.source.avatarUrl, feedBase.source.vip);
        mNickName.setText(feedBase.source.nickName);
        mTime.setText(DateTimeUtil.INSTANCE.getMessageReceiveTime(mTime.getResources(), feedBase.created));
        mNickName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(feedBase.source.id)) {
                    UserHostPage.launch(true, feedBase.source.id);
                }
            }
        });
        mHeadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(feedBase.source.id)) {
                    UserHostPage.launch(true, feedBase.source.id);
                }
            }
        });
        initRemindText(feedBase);
//        render(feedBase);
//        if (feedBase instanceof CommentNotification) {
//            parseCommentNotification(((CommentNotification) feedBase), ((CommentNotification) feedBase).getComment());
//        } else if (feedBase instanceof PostNotification) {
//            parsePostNotification(((PostNotification) feedBase));
//        } else if (feedBase instanceof ReplyNotification) {
//            parseReplyNotification(((ReplyNotification) feedBase), ((ReplyNotification) feedBase).getComment(), ((ReplyNotification) feedBase).getRelpy());
//        }
    }

//    private void render(final KMNotify notify){
//        if (TextUtils.equals(notify.action, FORWARD_POST)) {
//            mMessageText.setVisibility(View.VISIBLE);
//            mMessageText.getRichProcessor().setRichContent(notify.content.summary, null);
//            if (notify.content.deleted){
//                mPlayBtn.setVisibility(View.GONE);
//                mSummary.setVisibility(View.GONE);
//                mMessageCardImg.setImageResource(R.drawable.message_error_icon);
//            }else {
//                MessageCardUrlLoader.getInstance().loadMessageCardUrl(mMessageCardImg, ((PicPost) postBase).getPicInfo(0).getThumbUrl());
//                parsePost(((ForwardPost) postBase).getRootPost());
//                mCardView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        FeedDetailPage.launch(((ForwardPost) postBase).getRootPost());
//                    }
//                });
//            }
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    FeedDetailPage.launch(notify.content.id);
//                }
//            });
//        } else if (TextUtils.equals(notify.action, POST_AT)) {
//        } else if (TextUtils.equals(notify.action, LIKE_POST)) {
//
//        } else if (TextUtils.equals(notify.action, COMMENT_AT)) {
//        } else if (TextUtils.equals(notify.action, COMMENT)) {
//        } else if (TextUtils.equals(notify.action, LIKE_COMMENT)) {
//
//        } else if (TextUtils.equals(notify.action, REPLY_AT)) {
//        } else if (TextUtils.equals(notify.action, REPLY)) {
//        } else if (TextUtils.equals(notify.action, LIKE_REPLY)) {
//        } else if (TextUtils.equals(notify.action, FORWARD_COMMENT)) {
//        }
//    }



//    private void parseReplyNotification(final ReplyNotification feedBase, final Comment comment, Comment relpy) {
//        mMessageText.setVisibility(View.VISIBLE);
//        mMessageText.getRichProcessor().setRichContent(relpy.getContent(), relpy.getRichItemList());
//        mPlayBtn.setVisibility(View.GONE);
//        itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CommentDetailPage.launch(feedBase.getPost(), comment);
//            }
//        });
//        PostBase postBase = feedBase.getPost();
//        if (postBase instanceof ForwardPost) {
//            mMessageCardImgLayout.setVisibility(View.GONE);
//            mMessageCardText.getRichProcessor().setRichContent(postBase.getSummary(), postBase.getRichItemList());
//        } else {
//            parsePost(feedBase.getPost());
//        }
//    }

//    private void parseCommentNotification(final CommentNotification feedBase, Comment comment) {
//        mMessageText.setVisibility(View.VISIBLE);
//        mMessageText.getRichProcessor().setRichContent(comment.getContent(), comment.getRichItemList());
//        itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FeedDetailPage.launch(feedBase.getPost());
//            }
//        });
//        PostBase postBase = feedBase.getPost();
//        if (postBase instanceof ForwardPost) {
//            mMessageCardImgLayout.setVisibility(View.GONE);
//            mMessageCardText.getRichProcessor().setRichContent(postBase.getSummary(), postBase.getRichItemList());
//        } else {
//            parsePost(feedBase.getPost());
//        }
//    }

    private void parsePostNotification(final PostNotification feedBase) {
        final PostBase postBase = feedBase.getPost();
        if (postBase instanceof ForwardPost) {
//            mMessageText.setVisibility(View.VISIBLE);
//            mMessageText.getRichProcessor().setRichContent(postBase.getSummary(), postBase.getRichItemList());
//            if (((ForwardPost) postBase).isForwardDeleted()) {
//                mMessageCardImgLayout.setVisibility(View.VISIBLE);
//                mPlayBtn.setVisibility(View.GONE);
//                mMessageCardImg.setImageResource(R.drawable.message_error_icon);
//                mMessageCardText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "forward_feed_delete_content"));
//            } else {
//                parsePost(((ForwardPost) postBase).getRootPost());
//                mCardView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        FeedDetailPage.launch(((ForwardPost) postBase).getRootPost());
//                    }
//                });
//            }
        } else {
            mMessageText.setVisibility(View.GONE);
//            parsePost(postBase);
        }
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedDetailActivity.launch(feedBase.getPost());
            }
        });
    }

    private void renderPost(KMMagic data){
//        if (data.)
    }

//    private void parsePost(final PostBase postBase) {
//        mPlayBtn.setVisibility(View.GONE);
//        if (postBase instanceof PicPost) {
//            if (((PicPost) postBase).picCount() == 0) {
//                mMessageCardImgLayout.setVisibility(View.GONE);
//            } else {
//                mMessageCardImgLayout.setVisibility(View.VISIBLE);
//                MessageCardUrlLoader.getInstance().loadMessageCardUrl(mMessageCardImg, ((PicPost) postBase).getPicInfo(0).getThumbUrl());
//            }
//        } else if (postBase instanceof TextPost) {
//            mMessageCardImgLayout.setVisibility(View.GONE);
//        } else if (postBase instanceof VideoPost) {
//
//            if (TextUtils.isEmpty(((VideoPost) postBase).getCoverUrl())) {
//                mMessageCardImgLayout.setVisibility(View.GONE);
//            } else {
//                mPlayBtn.setVisibility(View.VISIBLE);
//                mMessageCardImgLayout.setVisibility(View.VISIBLE);
//                MessageCardUrlLoader.getInstance().loadMessageCardUrl(mMessageCardImg, ((VideoPost) postBase).getCoverUrl());
//            }
//        } else if (postBase instanceof LinkPost) {
//
//            if (TextUtils.isEmpty(((LinkPost) postBase).getLinkThumbUrl())) {
//                mMessageCardImgLayout.setVisibility(View.GONE);
//            } else {
//                mMessageCardImgLayout.setVisibility(View.VISIBLE);
//                MessageCardUrlLoader.getInstance().loadMessageCardUrl(mMessageCardImg, ((LinkPost) postBase).getLinkThumbUrl());
//            }
//        }
//        mMessageCardText.getRichProcessor().setRichContent(postBase.getSummary(), postBase.getRichItemList());
//        mCardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FeedDetailPage.launch(postBase);
//            }
//        });
//    }

    private String getRemind(KMNotify feedBase) {
        //TODO check word
        String key = "message_comment_mentioned_you";
        if (TextUtils.equals(feedBase.action, FORWARD_POST)) {
            key = "message_comment_mentioned_you";
        } else if (TextUtils.equals(feedBase.action, POST_AT)) {
            key = "message_comment_mentioned_you";
        } else if (TextUtils.equals(feedBase.action, COMMENT_AT)) {
            key = "message_comment_mentioned_you";
        } else if (TextUtils.equals(feedBase.action, REPLY_AT)) {
            key = "message_comment_mentioned_you";
        } else if (TextUtils.equals(feedBase.action, COMMENT)) {
            key = "message_comment_mentioned_you";
        } else if (TextUtils.equals(feedBase.action, REPLY)) {
            key = "message_comment_mentioned_you";
        } else if (TextUtils.equals(feedBase.action, LIKE_COMMENT)) {
            key = "message_comment_mentioned_you";
        } else if (TextUtils.equals(feedBase.action, LIKE_POST)) {
            key = "message_comment_mentioned_you";
        } else if (TextUtils.equals(feedBase.action, LIKE_REPLY)) {
            key = "message_comment_mentioned_you";
        } else if (TextUtils.equals(feedBase.action, FORWARD_COMMENT)) {
            key = "message_comment_mentioned_you";
        }
        return ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, key);
    }

    private void initRemindText(KMNotify feedBase) {
        String word = getRemind(feedBase);
        mRemindText.setText(word);
        mRemindText2.setText(word);

        if (!TextUtils.isEmpty(word)) {
            int remindStrLength = (int) mRemindText.getPaint().measureText(word);
            int nickNameStrLength = (int) mNickName.getPaint().measureText(feedBase.source.nickName);
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
