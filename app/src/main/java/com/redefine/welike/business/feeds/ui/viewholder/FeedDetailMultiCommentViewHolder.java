package com.redefine.welike.business.feeds.ui.viewholder;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.redefine.foundation.framework.Event;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.richtext.RichContent;
import com.redefine.richtext.RichTextView;
import com.redefine.richtext.block.Block;
import com.redefine.richtext.block.BlockFactory;
import com.redefine.richtext.helper.RichTextHelper;
import com.redefine.welike.R;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.feeds.management.bean.Comment;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.bean.FeedDetailCommentHeadBean;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MR on 2018/1/13.
 */

public class FeedDetailMultiCommentViewHolder extends FeedDetailCommentViewHolder {

    private final ViewGroup mSecondLevelContainer;
    private final LayoutInflater mInflater;
    private final String mMoreCommentStr;
    private FeedDetailCommentHeadBean.CommentSortType mSortType;

    public FeedDetailMultiCommentViewHolder(View itemView, PostBase postBase, FeedDetailCommentHeadBean.CommentSortType sortType) {
        super(itemView, postBase);
        mSecondLevelContainer = itemView.findViewById(R.id.feed_detail_multi_comment_container);
        mInflater = LayoutInflater.from(itemView.getContext());
        mMoreCommentStr = ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "feed_detail_multi_comment_more");
        mSortType = sortType;
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, Comment comment) {
        super.bindViews(adapter, comment);
        mSecondLevelContainer.removeAllViews();
        showCommentItem(comment, comment.getChildren());
        if (comment.getChildrenCount() > GlobalConfig.FEED_DETAIL_COMMENT_MAX_SHOW) {
            showMoreComment(comment);
        }
    }

    private void showCommentItem(final Comment parent, List<Comment> children) {
        if (CollectionUtil.isEmpty(children)) {
            return;
        }
        View rootView;
        RichTextView view;
        Comment comment;
        for (int i = 0; i < children.size() && i < GlobalConfig.FEED_DETAIL_COMMENT_MAX_SHOW; i++) {
            comment = children.get(i);
            rootView = mInflater.inflate(R.layout.feed_detail_multi_comment_cell, null);
            view = rootView.findViewById(R.id.feed_detail_multi_comment_content);
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (iBrowseFeedDetailClickListener != null) {
                        iBrowseFeedDetailClickListener.onBrowseFeedDetailClick(BrowseConstant.TYPE_UNKOWN,true);
                        return;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(FeedConstant.KEY_COMMENT, parent);
                    bundle.putSerializable(FeedConstant.KEY_FEED, mFeed);
                    bundle.putBoolean(FeedConstant.KEY_COMMENT_TYPE, mSortType == FeedDetailCommentHeadBean.CommentSortType.CREATED);
                    EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_COMMENT_DETAIL_EVENT, bundle));
                }
            });

            String content = comment.getContent();

            String nickName = comment.getNickName() == null ? comment.getUid() : comment.getNickName();
            Block block = BlockFactory.createMentionWithOutFlag(comment.getUid(), nickName);

            RichContent userNickContent = new RichContent();
            userNickContent.text = block.blockText;
            userNickContent.richItemList = new ArrayList<>();
            userNickContent.richItemList.add(block.richItem);
            RichContent richContent = RichTextHelper.mergeRichText(userNickContent, ":");

            RichContent postRichText = new RichContent();
            postRichText.text = content;
            postRichText.richItemList = comment.getRichItemList();
            richContent = RichTextHelper.mergeRichText(richContent, postRichText);

            view.getRichProcessor().setRichContent(richContent.text, richContent.richItemList);

            if (i == 0) {
                rootView.setPadding(0, 0, 0, 0);
            }
            mSecondLevelContainer.addView(rootView);
        }
    }

    private void showMoreComment(final Comment comment) {
        View rootView = mInflater.inflate(R.layout.feed_detail_multi_comment_more, null);
        TextView view = rootView.findViewById(R.id.feed_detail_multi_comment_more_view);
        view.setText(String.valueOf(comment.getChildrenCount() - GlobalConfig.FEED_DETAIL_COMMENT_MAX_SHOW) + mMoreCommentStr);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iBrowseFeedDetailClickListener != null) {
                    iBrowseFeedDetailClickListener.onBrowseFeedDetailClick(BrowseConstant.TYPE_UNKOWN,true);
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable(FeedConstant.KEY_COMMENT, comment);
                bundle.putSerializable(FeedConstant.KEY_FEED, mFeed);
                EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_COMMENT_DETAIL_EVENT, bundle));
            }
        });
        mSecondLevelContainer.addView(rootView);
    }
}
