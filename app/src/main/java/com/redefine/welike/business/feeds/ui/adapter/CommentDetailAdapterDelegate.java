package com.redefine.welike.business.feeds.ui.adapter;

import com.redefine.commonui.loadmore.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.redefine.commonui.loadmore.adapter.OnClickRetryListener;
import com.redefine.welike.business.feeds.management.bean.Comment;
import com.redefine.welike.business.feeds.ui.bean.FeedDetailCommentHeadBean;
import com.redefine.welike.business.feeds.ui.listener.IFeedDetailCommentOpListener;
import com.redefine.welike.business.feeds.ui.viewholder.CommentDetailReplyViewHolder;

import java.util.List;

/**
 * Created by zhl on 2018/4/18.
 */

public class CommentDetailAdapterDelegate {

    private final CommentDetailAdapter mCreatedAdapter;
    private final CommentDetailAdapter mHotAdapter;
    private FeedDetailCommentHeadBean.CommentSortType mSortType;

    public CommentDetailAdapterDelegate(FeedDetailCommentHeadBean.CommentSortType sortType
            , IFeedDetailCommentOpListener listener
            , OnClickRetryListener retryListener
            , CommentDetailReplyViewHolder.OnCommentLikeListener commentLikeListener) {
        mSortType = sortType;
        mCreatedAdapter = new CommentDetailAdapter(FeedDetailCommentHeadBean.CommentSortType.CREATED, listener, commentLikeListener);
        mHotAdapter = new CommentDetailAdapter(FeedDetailCommentHeadBean.CommentSortType.HOT, listener, commentLikeListener);
        mCreatedAdapter.setRetryLoadMoreListener(retryListener);
        mHotAdapter.setRetryLoadMoreListener(retryListener);
    }

//    public void setFeedBase(PostBase mPostBase) {
//        mCreatedAdapter.setFeedBase(mPostBase);
//        mHotAdapter.setFeedBase(mPostBase);
//    }

    public CommentDetailAdapter getAdapter() {
        if (mSortType == FeedDetailCommentHeadBean.CommentSortType.HOT) {
            return mHotAdapter;
        }
        return mCreatedAdapter;
    }

    public void addNewData(List<Comment> comments) {
        getAdapter().addHisData(comments);
    }

    public void clearFinishFlag() {
        getAdapter().clearFinishFlag();
    }

    public int getRealItemCount() {
        return getAdapter().getRealItemCount();
    }

    public void showFooter() {
        getAdapter().showFooter();
    }

    public void addHisData(List<Comment> comments) {
        getAdapter().addHisData(comments);
    }

    public void noMore() {
        getAdapter().noMore();
    }

    public void finishLoadMore() {
        getAdapter().finishLoadMore();
    }

    public void loadError() {
        getAdapter().loadError();
    }

    public CommentDetailAdapter getAdapter(FeedDetailCommentHeadBean.CommentSortType sortType) {
        if (sortType == FeedDetailCommentHeadBean.CommentSortType.HOT) {
            return mHotAdapter;
        }
        return mCreatedAdapter;
    }

    public boolean canLoadMore() {
        return getAdapter().canLoadMore();
    }

    public void onLoadMore() {
        getAdapter().onLoadMore();
    }

    public void hideFooter() {
        getAdapter().hideFooter();
    }

    public void refreshComment(Comment comment) {
        getAdapter().refreshComment(comment);
    }

    public void deleteComment(int value) {
        mHotAdapter.deleteItem(value);
        mCreatedAdapter.deleteItem(value);
    }

    public void setSortType(FeedDetailCommentHeadBean.CommentSortType sortType) {
        mSortType = sortType;
        if (sortType == FeedDetailCommentHeadBean.CommentSortType.HOT) {
            mHotAdapter.clearFinishFlag();
        } else {
            mCreatedAdapter.clearFinishFlag();
        }
    }

    public void setOnItemClickListener(HeaderAndFooterRecyclerViewAdapter.OnItemClickListener onItemClickListener) {
        if (onItemClickListener != null) {
            mCreatedAdapter.setOnItemClickListener(onItemClickListener);
            mHotAdapter.setOnItemClickListener(onItemClickListener);
        }
    }
}
