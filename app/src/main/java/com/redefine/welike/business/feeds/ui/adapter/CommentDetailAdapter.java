package com.redefine.welike.business.feeds.ui.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.redefine.commonui.loadmore.adapter.LoadMoreFooterRecyclerAdapter;
import com.redefine.commonui.loadmore.bean.BaseHeaderBean;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.commonui.loadmore.viewholder.WhiteBgLoadMoreViewHolder;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;
import com.redefine.welike.business.feeds.management.bean.Comment;
import com.redefine.welike.business.feeds.ui.bean.FeedDetailCommentHeadBean;
import com.redefine.welike.business.feeds.ui.listener.IFeedDetailCommentOpListener;
import com.redefine.welike.business.feeds.ui.viewholder.CommentDetailReplyViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.FeedDetailCommentHeaderViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MR on 2018/1/16.
 */

public class CommentDetailAdapter extends LoadMoreFooterRecyclerAdapter<BaseHeaderBean, Comment> {
    private final List<Comment> mComments = new ArrayList<Comment>();

    private IFeedDetailCommentOpListener mListener;
    private CommentDetailReplyViewHolder.OnCommentLikeListener mCommentLikeListener;

    public CommentDetailAdapter() {
    }

    public CommentDetailAdapter(FeedDetailCommentHeadBean.CommentSortType sortType, IFeedDetailCommentOpListener listener,
                                CommentDetailReplyViewHolder.OnCommentLikeListener commentLikeListener) {
        setHeader(new FeedDetailCommentHeadBean(sortType));
        mListener = listener;
        mCommentLikeListener = commentLikeListener;
    }

    @Override
    protected BaseRecyclerViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        return new WhiteBgLoadMoreViewHolder(mInflater.inflate(R.layout.load_more_layout, parent, false));
    }

    @Override
    protected BaseRecyclerViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        return new FeedDetailCommentHeaderViewHolder(mInflater.inflate(R.layout.feed_detail_comment_head, parent, false), mListener);
    }

    @Override
    protected void onBindHeaderViewHolder(BaseRecyclerViewHolder holder, int position) {
        holder.bindViews(this, getHeader());
    }

    @Override
    protected BaseRecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new CommentDetailReplyViewHolder(mInflater.inflate(R.layout.comment_detail_reply_comment, parent, false), mCommentLikeListener);
    }

    @Override
    protected void onBindItemViewHolder(BaseRecyclerViewHolder holder, int position) {
        if (position == mComments.size() - 1) {
            if (holder instanceof CommentDetailReplyViewHolder) {
                ((CommentDetailReplyViewHolder) holder).showShadowView(true);
            }
        } else {
            if (holder instanceof CommentDetailReplyViewHolder) {
                ((CommentDetailReplyViewHolder) holder).showShadowView(false);
            }
        }

        holder.bindViews(this, getRealItem(position));
    }

    @Override
    protected int getRealItemViewType(int position) {
        return 0;
    }

    @Override
    public int getRealItemCount() {
        return mComments.size();
    }

    @Override
    protected Comment getRealItem(int position) {
        return mComments.get(position);
    }

    public void addHisData(List<Comment> comment) {
        if (CollectionUtil.isEmpty(comment)) {
            return;
        }
        mComments.addAll(comment);
        notifyDataSetChanged();
    }

    public void refreshComment(Comment comment) {
        if (CollectionUtil.isEmpty(comment.getChildren())) {
            return;
        }
        mComments.add(0, comment.getChildren().get(0));
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        mComments.remove(position);
        notifyDataSetChanged();
    }
}
