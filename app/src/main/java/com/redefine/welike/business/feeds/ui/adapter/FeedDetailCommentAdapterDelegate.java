package com.redefine.welike.business.feeds.ui.adapter;

import com.redefine.commonui.loadmore.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.redefine.commonui.loadmore.adapter.OnClickRetryListener;
import com.redefine.welike.business.browse.ui.listener.IBrowseFeedDetailClickListener;
import com.redefine.welike.business.feeds.management.bean.Comment;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.bean.FeedDetailCommentHeadBean;
import com.redefine.welike.business.feeds.ui.listener.IFeedDetailCommentOpListener;
import com.redefine.welike.business.feeds.ui.viewholder.FeedDetailCommentViewHolder;

import java.util.List;

/**
 * Created by liwenbo on 2018/4/18.
 */

public class FeedDetailCommentAdapterDelegate {

    private final FeedDetailCommentAdapter mCreatedAdapter;
    private final FeedDetailCommentAdapter mHotAdapter;
    private FeedDetailCommentHeadBean.CommentSortType mSortType;

    public FeedDetailCommentAdapterDelegate(FeedDetailCommentHeadBean.CommentSortType sortType
            , OnClickRetryListener retryListener) {
        mSortType = sortType;
        mCreatedAdapter = new FeedDetailCommentAdapter(FeedDetailCommentHeadBean.CommentSortType.CREATED);
        mHotAdapter = new FeedDetailCommentAdapter(FeedDetailCommentHeadBean.CommentSortType.HOT);
        mCreatedAdapter.setRetryLoadMoreListener(retryListener);
        mHotAdapter.setRetryLoadMoreListener(retryListener);
    }

    public FeedDetailCommentAdapterDelegate(FeedDetailCommentHeadBean.CommentSortType sortType
            , IFeedDetailCommentOpListener listener
            , OnClickRetryListener retryListener) {
        mSortType = sortType;
        mCreatedAdapter = new FeedDetailCommentAdapter(FeedDetailCommentHeadBean.CommentSortType.CREATED,listener);
        mHotAdapter = new FeedDetailCommentAdapter(FeedDetailCommentHeadBean.CommentSortType.HOT,listener);
        mCreatedAdapter.setRetryLoadMoreListener(retryListener);
        mHotAdapter.setRetryLoadMoreListener(retryListener);
    }

    public void setBrowseFeedDetailClickListener(IBrowseFeedDetailClickListener iBrowseFeedDetailClickListener) {
        mCreatedAdapter.setBrowseFeedDetailClickListener(iBrowseFeedDetailClickListener);
        mHotAdapter.setBrowseFeedDetailClickListener(iBrowseFeedDetailClickListener);

    }

    public void setFeedBase(PostBase mPostBase) {
        mCreatedAdapter.setFeedBase(mPostBase);
        mHotAdapter.setFeedBase(mPostBase);
    }

    public FeedDetailCommentAdapter getAdapter() {
        if (mSortType == FeedDetailCommentHeadBean.CommentSortType.HOT) {
            return mHotAdapter;
        }
        return mCreatedAdapter;
    }

    public void addNewData(List<Comment> comments) {
        getAdapter().addNewData(comments);
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

    public FeedDetailCommentAdapter getAdapter(FeedDetailCommentHeadBean.CommentSortType sortType) {
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

    public void deleteComment(String value) {
        mHotAdapter.deleteComment(value);
        mCreatedAdapter.deleteComment(value);
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

    public void setOnCommentOperationListener(FeedDetailCommentViewHolder.OnCommentOperationListener listener) {
        mCreatedAdapter.setOnCommentOperationListener(listener);
        mHotAdapter.setOnCommentOperationListener(listener);
    }
}
