package com.redefine.welike.business.feeds.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.redefine.commonui.loadmore.adapter.LoadMoreFooterRecyclerAdapter;
import com.redefine.commonui.loadmore.bean.BaseHeaderBean;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.business.browse.ui.listener.IBrowseFeedDetailClickListener;
import com.redefine.welike.business.feeds.management.bean.Comment;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.bean.FeedDetailCommentHeadBean;
import com.redefine.welike.business.feeds.ui.listener.IFeedDetailCommentOpListener;
import com.redefine.welike.business.feeds.ui.viewholder.BaseCommentViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.FeedDetailCommentHeaderViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.FeedDetailCommentViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.FeedDetailMultiCommentViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.FeedLoadMoreViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwb on 2018/1/12.
 */

public class FeedDetailCommentAdapter extends LoadMoreFooterRecyclerAdapter<BaseHeaderBean, Comment> {

    private IFeedDetailCommentOpListener mListener;
    private boolean isShowCommentHeadSwitchView = false;
    private LayoutInflater mLayoutInflater;
    private List<Comment> mData = new ArrayList<Comment>();
    private static final int COMMENT_FIRST_LEVEL = 0;
    private static final int COMMENT_SECOND_LEVEL = 1;
    private PostBase mPostBase;
    private FeedDetailCommentViewHolder.OnCommentOperationListener mCommentOperationListener;
    protected FeedDetailCommentHeadBean.CommentSortType sortType;

    private IBrowseFeedDetailClickListener iBrowseFeedDetailClickListener;

    public FeedDetailCommentAdapter(FeedDetailCommentHeadBean.CommentSortType sortType) {
        this.sortType = sortType;
    }

    public FeedDetailCommentAdapter(FeedDetailCommentHeadBean.CommentSortType sortType, IFeedDetailCommentOpListener listener) {
        setHeader(new FeedDetailCommentHeadBean(sortType));
        mListener = listener;
        isShowCommentHeadSwitchView = true;
        this.sortType = sortType;
    }

    @Override
    protected BaseRecyclerViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        return new FeedLoadMoreViewHolder(mInflater.inflate(R.layout.feed_load_more_layout, parent, false));
    }

    @Override
    protected BaseRecyclerViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        if (isShowCommentHeadSwitchView)
            return new FeedDetailCommentHeaderViewHolder(mInflater.inflate(R.layout.feed_detail_comment_head, parent, false), mListener);
        return null;
    }

    @Override
    protected void onBindHeaderViewHolder(BaseRecyclerViewHolder holder, int position) {
        if (isShowCommentHeadSwitchView)
            holder.bindViews(this, getHeader());
    }

    @Override
    protected BaseRecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }
        BaseCommentViewHolder commentViewHolder;
        switch (viewType) {
            case COMMENT_FIRST_LEVEL:
                commentViewHolder = new FeedDetailCommentViewHolder(mLayoutInflater.inflate(R.layout.feed_detail_comment_item, parent, false), mPostBase);
                break;
            case COMMENT_SECOND_LEVEL:
                commentViewHolder = new FeedDetailMultiCommentViewHolder(mLayoutInflater.inflate(R.layout.feed_detail_multi_comment_item, parent, false), mPostBase, sortType);
                break;
            default:
                commentViewHolder = new FeedDetailCommentViewHolder(mLayoutInflater.inflate(R.layout.feed_detail_comment_item, parent, false), mPostBase);
                break;
        }
        return commentViewHolder;
    }

    public void setBrowseFeedDetailClickListener(IBrowseFeedDetailClickListener iBrowseFeedDetailClickListener) {
        this.iBrowseFeedDetailClickListener = iBrowseFeedDetailClickListener;
    }

    @Override
    protected void onBindItemViewHolder(BaseRecyclerViewHolder holder, int position) {
        holder.bindViews(this, mData.get(position));
        if (holder instanceof FeedDetailCommentViewHolder) {
            ((FeedDetailCommentViewHolder) holder).showShadowView(position == mData.size() - 1);
            ((FeedDetailCommentViewHolder) holder).setOnCommentOperationListener(mCommentOperationListener);
            ((FeedDetailCommentViewHolder) holder).setBrowseFeedDetailClickListener(iBrowseFeedDetailClickListener);
        }
    }

    @Override
    protected int getRealItemViewType(int position) {
        return CollectionUtil.getCount(mData.get(position).getChildren()) == 0 ? COMMENT_FIRST_LEVEL : COMMENT_SECOND_LEVEL;
    }

    @Override
    public int getRealItemCount() {
        return mData.size();
    }

    @Override
    protected Comment getRealItem(int position) {
        return mData.get(position);
    }

    public void addNewData(List<Comment> comments) {
        mData.clear();
        if (!CollectionUtil.isEmpty(comments)) {
            mData.addAll(0, comments);
        }
        notifyDataSetChanged();
    }

    public void addHisData(List<Comment> comments) {
        if (CollectionUtil.isEmpty(comments)) {
            return;
        }
        mData.addAll(comments);
        notifyDataSetChanged();
    }

    public void setFeedBase(PostBase postBase) {
        mPostBase = postBase;
    }

    public void refreshComment(Comment comment) {
        if (CollectionUtil.isEmpty(mData)) {
            mData.add(0, comment);
            notifyDataSetChanged();
            return;
        }
        boolean isExist = false;
        for (Comment c : mData) {
            if (TextUtils.equals(c.getCid(), comment.getCid())) {
                int childCount = CollectionUtil.getCount(c.getChildren());
                if (childCount < GlobalConfig.FEED_DETAIL_COMMENT_MAX_SHOW) {
                    if (CollectionUtil.getCount(comment.getChildren()) > 0) {
                        c.addChild(0, comment.getChildren().get(0));
                        c.setChildrenCount(c.getChildrenCount() + 1);
                    }
                } else {
                    c.getChildren().remove(0);
                    c.addChild(0, comment.getChildren().get(0));
                    c.setChildrenCount(c.getChildrenCount() + 1);
                }
                isExist = true;
            }
        }
        if (!isExist) {
            mData.add(0, comment);
        }
        notifyDataSetChanged();
    }

    public void deleteComment(String id) {
        int index = 0;
        int update = 0;//0 = nothing; 1 = delete; 2 = update;
        for (Comment t : mData) {
            if (t.getCid().equalsIgnoreCase(id)) {
                //delete first level comment.
                update = 1;
                mData.remove(t);
                break;
            }
            //try to find in comment's reply.
            List<Comment> children = t.getChildren();
            if (children != null) {
                for (Comment reply : children) {
                    if (reply.getCid().equalsIgnoreCase(id)) {
                        update = 2;
                        children.remove(reply);
                        break; //Must break at HERE! if U remove item in loop.
                    }
                }
            }
            if (update > 0) {
                break;
            }
            index++;
        }
        if (update == 1) {
            notifyItemRemoved(getAdapterItemPosition(index));
        } else if (update == 2) {
            notifyItemChanged(getAdapterItemPosition(index));
        }
    }

    public void setOnCommentOperationListener(FeedDetailCommentViewHolder.OnCommentOperationListener listener) {
        mCommentOperationListener = listener;
    }
}
