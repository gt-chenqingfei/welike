package com.redefine.welike.business.feeds.ui.viewholder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.framework.Event;
import com.redefine.richtext.RichTextView;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.base.util.TimeUtil;
import com.redefine.welike.business.feeds.management.bean.Comment;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;
import com.redefine.welike.business.feeds.ui.util.DefaultRichItemClickHandler;
import com.redefine.welike.business.user.ui.page.UserHostPage;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.common.util.DateTimeUtil;
import com.redefine.welike.commonui.widget.VipAvatar;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by MR on 2018/1/16.
 */

public class CommentDetailMainViewHolder extends BaseRecyclerViewHolder<Comment> implements View.OnClickListener {

    private final VipAvatar mUserHeader;
    private final TextView mUserNick;
    private final RichTextView mCommentContent;
    private final TextView mCommentTime;
    private final TextView mViewBlog;
    private final PostBase mFeedBase;

    public CommentDetailMainViewHolder(View itemView, PostBase postBase) {
        super(itemView);
        mFeedBase = postBase;
        mUserHeader = (VipAvatar) itemView.findViewById(R.id.comment_detail_main_user_header_view);
        mUserNick = (TextView) itemView.findViewById(R.id.comment_detail_main_user_nick);
        mCommentContent = (RichTextView) itemView.findViewById(R.id.comment_detail_main_content);
        mCommentTime = (TextView) itemView.findViewById(R.id.comment_detail_main_time);
        mViewBlog = (TextView) itemView.findViewById(R.id.comment_detail_main_view_blog);
        mViewBlog.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "comment_detail_view_blog"));
    }

    @Override
    public void bindViews(@Nullable RecyclerView.Adapter adapter, final Comment comment) {
        super.bindViews(adapter, comment);
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
        mViewBlog.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mViewBlog) {
            if (mFeedBase != null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(FeedConstant.KEY_FEED, mFeedBase);
                EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_FEED_DETAIL_EVENT, bundle));
            }
        }
    }
}
