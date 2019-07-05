package com.redefine.welike.business.feeds.ui.viewholder;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.richtext.RichTextView;
import com.redefine.welike.R;
import com.redefine.welike.base.util.TimeUtil;
import com.redefine.welike.business.common.LikeManager;
import com.redefine.welike.business.feeds.management.CommentsManager;
import com.redefine.welike.business.feeds.management.bean.Comment;
import com.redefine.welike.business.feeds.ui.util.DefaultRichItemClickHandler;
import com.redefine.welike.business.user.ui.page.UserHostPage;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.common.util.DateTimeUtil;
import com.redefine.welike.commonui.widget.VipAvatar;

/**
 * Created by MR on 2018/1/16.
 */

public class CommentDetailReplyViewHolder extends BaseRecyclerViewHolder<Comment> implements View.OnClickListener {
    private final VipAvatar mUserHeader;
    private final TextView mUserNick;
    public final RichTextView mCommentContent;
    private final TextView mCommentTime;
    private final TextView mCommentLinkCount;
    private final View mCommentLinkContainer;
    private final ImageView mCommentLikeImageView;
    private final View mCommentBottomLine;
    public final ViewGroup mCommentBottomShadowView;
    private Comment mComment;
    private OnCommentLikeListener mListener;

    public CommentDetailReplyViewHolder(View itemView, OnCommentLikeListener listener) {
        super(itemView);
        mUserHeader = itemView.findViewById(R.id.comment_detail_reply_user_head);
        mUserNick = itemView.findViewById(R.id.comment_detail_reply_user_nick);
        mCommentContent = itemView.findViewById(R.id.comment_detail_reply_content);
        mCommentTime = itemView.findViewById(R.id.comment_detail_reply_time);
        mCommentLinkCount = itemView.findViewById(R.id.comment_detail_reply_like);
        mCommentLinkContainer = itemView.findViewById(R.id.comment_detail_reply_like_container);
        mCommentLikeImageView = itemView.findViewById(R.id.comment_detail_reply_like_view);
        mCommentBottomShadowView = itemView.findViewById(R.id.common_feed_bottom_root_shadow);
        mCommentBottomLine = itemView.findViewById(R.id.comment_detail_reply_line);
        mCommentLinkContainer.setOnClickListener(this);
        mListener = listener;
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, final Comment comment) {
        super.bindViews(adapter, comment);
        mComment = comment;
//        HeadUrlLoader.getInstance().loadHeaderUrl(mUserHeader, comment.getHead());
        VipUtil.set(mUserHeader, comment.getHead(), comment.getVip());
        VipUtil.setNickName(mUserNick, comment.getCurLevel(), comment.getNickName());

        mCommentContent.getRichProcessor().setOnRichItemClickListener(new DefaultRichItemClickHandler(mCommentContent.getContext()));

        mUserHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserHostPage.launch(true, comment.getUid());
            }
        });
        mCommentContent.getRichProcessor().setRichContent(comment.getContent(), comment.getRichItemList());
        mCommentTime.setText(DateTimeUtil.INSTANCE.formatPostPublishTime(mCommentTime.getResources(), comment.getTime()));
        mCommentLinkCount.setSelected(comment.isLike());
        mCommentLikeImageView.setImageResource(LikeManager.getImage(mComment.isLike() ? 1 : 0));
        mCommentLinkCount.setText(comment.getLikeCount() > 0 ? String.valueOf(comment.getLikeCount()) : "");
        mCommentLinkCount.setTextColor(ContextCompat.getColor(mCommentLinkCount.getContext(), comment.isLike()
                ? R.color.feed_flag_trending_text_color : R.color.feed_detail_item_like_text_color));
    }

    @Override
    public void onClick(View v) {
        if (v == mCommentLinkContainer) {
            // do like
            doLikeBtnClick();
        }
    }

    public void doLikeBtnClick() {
        if (!mComment.isLike()) {
            CommentsManager.likeReply(mComment.getCid());
            mComment.setLike(true);
            mComment.setLikeCount(mComment.getLikeCount() + 1);
            if (mListener != null) {
                mListener.onCommentLike(mComment);
            }
        }
        mCommentLikeImageView.setImageResource(LikeManager.getImage(mComment.isLike() ? 1 : 0));
        mCommentLinkCount.setText(mComment.getLikeCount() > 0 ? String.valueOf(mComment.getLikeCount()) : "");
        mCommentLinkCount.setTextColor(ContextCompat.getColor(mCommentLinkCount.getContext(), mComment.isLike()
                ? R.color.feed_flag_trending_text_color : R.color.feed_detail_item_like_text_color));
    }

    public interface OnCommentLikeListener {
        void onCommentLike(Comment comment);
    }

    public void showShadowView(boolean isShow) {
        mCommentBottomShadowView.setVisibility(isShow ? View.VISIBLE : View.GONE);
        mCommentBottomLine.setVisibility(isShow ? View.GONE : View.VISIBLE);
    }
}
