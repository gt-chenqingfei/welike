package com.redefine.welike.business.feeds.ui.viewholder;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.redefine.commonui.h5.WebViewActivity;
import com.redefine.foundation.framework.Event;
import com.redefine.richtext.RichItem;
import com.redefine.richtext.RichTextView;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.util.TimeUtil;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.browse.ui.listener.IBrowseFeedDetailClickListener;
import com.redefine.welike.business.common.LikeManager;
import com.redefine.welike.business.feeds.management.CommentsManager;
import com.redefine.welike.business.feeds.management.bean.Comment;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.util.DefaultRichItemClickHandler;
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean;
import com.redefine.welike.business.publisher.ui.activity.PublishForwardStarter;
import com.redefine.welike.business.publisher.ui.activity.PublishReplyStarter;
import com.redefine.welike.business.topic.ui.constant.TopicConstant;
import com.redefine.welike.business.user.ui.page.UserHostPage;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.common.util.DateTimeUtil;
import com.redefine.welike.commonui.widget.VipAvatar;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by MR on 2018/1/13.
 */

public class FeedDetailCommentViewHolder extends BaseCommentViewHolder implements View.OnClickListener {

    private final VipAvatar mUserHeader;
    private final TextView mUserNick;
    public final RichTextView mCommentContent;
    private final TextView mCommentTime;
    private final TextView mCommentLinkCount;
    private ImageView mCommonLikeIcon;
    protected final PostBase mFeed;
    private final View mCommentLinkContainer;
    private final View mCommentReplyContainer;
    private final View mCommentForwardContainer;
    private final ViewGroup mCommentBottomShadowView;
    private Comment mComment;
    private OnCommentOperationListener mListener;
    protected IBrowseFeedDetailClickListener iBrowseFeedDetailClickListener;

    public FeedDetailCommentViewHolder(View itemView, PostBase postBase) {
        super(itemView);
        mFeed = postBase;
        mUserHeader = itemView.findViewById(R.id.feed_detail_comment_user_head);
        mUserNick = itemView.findViewById(R.id.feed_detail_comment_user_nick);
        mCommentContent = itemView.findViewById(R.id.feed_detail_comment_content);
        mCommentTime = itemView.findViewById(R.id.feed_detail_comment_time);
        mCommentLinkCount = itemView.findViewById(R.id.feed_detail_comment_item_like);
        mCommonLikeIcon = itemView.findViewById(R.id.common_comment_count_icon);
        mCommentBottomShadowView = itemView.findViewById(R.id.common_feed_bottom_root_shadow);
        mCommentLinkContainer = itemView.findViewById(R.id.feed_detail_comment_item_like_container);
        mCommentReplyContainer = itemView.findViewById(R.id.feed_detail_comment_item_comment_container);
        mCommentForwardContainer = itemView.findViewById(R.id.feed_detail_comment_item_forward_container);
        mCommentLinkContainer.setOnClickListener(this);
        mCommentReplyContainer.setOnClickListener(this);
        mCommentForwardContainer.setOnClickListener(this);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, final Comment comment) {
        super.bindViews(adapter, comment);
        mComment = comment;
//        HeadUrlLoader.getInstance().loadHeaderUrl(mUserHeader, comment.getHead());
        VipUtil.set(mUserHeader, comment.getHead(), comment.getVip());
        VipUtil.setNickName(mUserNick, comment.getCurLevel(), comment.getNickName());

        mCommentContent.getRichProcessor().setOnRichItemClickListener(new DefaultRichItemClickHandler(mCommentContent.getContext()) {
            @Override
            public void onRichItemClick(RichItem richItem) {
                if (richItem.isLinkItem()) {
                    String target = TextUtils.isEmpty(richItem.target) ? richItem.source : richItem.target;
                    WebViewActivity.luanch(mCommentContent.getContext(), target);
                } else if (richItem.isAtItem()) {
                    if (iBrowseFeedDetailClickListener != null) {
                        iBrowseFeedDetailClickListener.onBrowseFeedDetailClick(BrowseConstant.TYPE_UNKOWN, false);
                    }
                    UserHostPage.launch(true, richItem.id);
                } else if (richItem.isTopicItem()) {

                    if (TextUtils.isEmpty(richItem.id)) return;
                    Bundle bundle = new Bundle();
                    TopicSearchSugBean.TopicBean bean = new TopicSearchSugBean.TopicBean();
                    String target = TextUtils.isEmpty(richItem.display) ? richItem.source : richItem.display;
                    bean.name = target;
                    bean.id = richItem.id;
                    bundle.putSerializable(TopicConstant.BUNDLE_KEY_TOPIC, bean);
                    if (iBrowseFeedDetailClickListener != null) {
                        iBrowseFeedDetailClickListener.onBrowseFeedDetailClick(BrowseConstant.TYPE_UNKOWN, false);
                    }
                    EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_TOPIC_LANDING_PAGE, bundle));
                } else if (richItem.isSuperTopicItem()) {
                    Bundle bundle = new Bundle();
                    if (iBrowseFeedDetailClickListener != null) {
                        iBrowseFeedDetailClickListener.onBrowseFeedDetailClick(BrowseConstant.TYPE_UNKOWN, false);
                    }
                    bundle.putString(RouteConstant.ROUTE_KEY_SUPER_TOPIC_ID, richItem.id);
                    EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_SUPER_TOPIC_PAGE, bundle));
                }
            }
        });

        mCommentContent.getRichProcessor().setRichContent(comment.getContent(), comment.getRichItemList());
        mCommentTime.setText(DateTimeUtil.INSTANCE.formatPostPublishTime(mCommentTime.getResources(), comment.getTime()));

        mUserHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iBrowseFeedDetailClickListener != null) {
                    iBrowseFeedDetailClickListener.onBrowseFeedDetailClick(BrowseConstant.TYPE_UNKOWN, false);
                }
                UserHostPage.launch(true, comment.getUid());
            }
        });

        mUserNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iBrowseFeedDetailClickListener != null) {
                    iBrowseFeedDetailClickListener.onBrowseFeedDetailClick(BrowseConstant.TYPE_UNKOWN, false);
                }
                UserHostPage.launch(true, comment.getUid());
            }
        });
        updateLikeBtn();
    }

    private void doLikeBtnClick() {
        if (!mComment.isLike()) {
            CommentsManager.likeComment(mComment.getCid());
            mComment.setLike(true);
            mComment.setLikeCount(mComment.getLikeCount() + 1);
            updateLikeBtn();
            if (mListener != null) {
                mListener.onCommentLike(mComment);
            }
        }
    }

    private void updateLikeBtn() {
        mCommentLinkCount.setSelected(mComment.isLike());
        mCommentLinkCount.setText(mComment.getLikeCount() > 0 ? String.valueOf(mComment.getLikeCount()) : "");
        mCommentLinkCount.setTextColor(ContextCompat.getColor(mCommentLinkCount.getContext(), mComment.isLike()
                ? R.color.feed_flag_trending_text_color : R.color.feed_detail_item_like_text_color));

        mCommonLikeIcon.setPadding(0, 0, 0, 0);
        mCommonLikeIcon.setImageResource(LikeManager.getImage(mComment.isLike() ? 1 : 0));
    }

    @Override
    public void onClick(View v) {

        if (v == mCommentForwardContainer) {
            if (iBrowseFeedDetailClickListener != null) {
                iBrowseFeedDetailClickListener.onBrowseFeedDetailClick(BrowseConstant.TYPE_REPOST, true);
                return;
            }
            if (mComment != null) {
                PublishForwardStarter.INSTANCE.startActivity4CommentFromFeedDetail(v.getContext(), mFeed, mComment);
                if (mListener != null) {
                    mListener.onCommentForward(mComment);
                }
            }
        } else if (v == mCommentReplyContainer) {
            if (iBrowseFeedDetailClickListener != null) {
                iBrowseFeedDetailClickListener.onBrowseFeedDetailClick(BrowseConstant.TYPE_COMMENT, true);
                return;
            }
            if (mComment != null) {
                PublishReplyStarter.INSTANCE.startPopUpActivityFromCommentDetail((AppCompatActivity) v.getContext(), mComment);
                if (mListener != null) {
                    mListener.onCommentReply(mComment);
                }
            }
        } else if (v == mCommentLinkContainer) {
            if (iBrowseFeedDetailClickListener != null) {
                iBrowseFeedDetailClickListener.onBrowseFeedDetailClick(BrowseConstant.TYPE_LIKE, true);
                return;
            }
            // do like
            doLikeBtnClick();
        }
    }

    public void setOnCommentOperationListener(OnCommentOperationListener listener) {
        mListener = listener;
    }

    public void setBrowseFeedDetailClickListener(IBrowseFeedDetailClickListener iBrowseFeedDetailClickListener) {
        this.iBrowseFeedDetailClickListener = iBrowseFeedDetailClickListener;
    }

    public void showShadowView(boolean isShow) {
        mCommentBottomShadowView.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public interface OnCommentOperationListener {
        void onCommentReply(Comment comment);

        void onCommentForward(Comment comment);

        void onCommentLike(Comment comment);
    }
}
