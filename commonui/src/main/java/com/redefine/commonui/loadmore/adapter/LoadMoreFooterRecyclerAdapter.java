package com.redefine.commonui.loadmore.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.redefine.commonui.R;
import com.redefine.commonui.loadmore.bean.LoadMoreBean;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.commonui.loadmore.viewholder.LoadMoreViewHolder;


/**
 * Created by MR on 2018/1/15.
 */

public abstract class LoadMoreFooterRecyclerAdapter<H, T> extends HeaderAndFooterRecyclerViewAdapter<BaseRecyclerViewHolder, H, T, LoadMoreBean> implements HeaderAndFooterRecyclerViewAdapter.OnFooterItemClickListener {

    private boolean isLoadingMore = false;
    private OnClickRetryListener mRetryLoadMoreListener;
    private RecyclerView mRecycler;

    public LoadMoreFooterRecyclerAdapter() {
        setFooter(new LoadMoreBean());
        setOnFooterItemClickListener(this);
    }

    @Override
    protected BaseRecyclerViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        return new LoadMoreViewHolder(mInflater.inflate(R.layout.load_more_layout, parent, false));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onBindFooterViewHolder(BaseRecyclerViewHolder holder, int position) {
        holder.bindViews(this, getFooter());
    }

    public void onLoadMore() {
        isLoadingMore = true;
        getFooter().setState(LoadMoreBean.LoadingMoreState.LOADING);
        notifyItemChanged(getFooterPosition());
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mRecycler = null;
        super.onDetachedFromRecyclerView(recyclerView);
    }

    public void finishLoadMore() {
        isLoadingMore = false;
        getFooter().setState(LoadMoreBean.LoadingMoreState.LOADING);
        notifyItemChanged(getFooterPosition());
    }

    public void loadError() {
        isLoadingMore = false;
        getFooter().setState(LoadMoreBean.LoadingMoreState.LOADED_ERROR);
        notifyItemChanged(getFooterPosition());
    }

    public void noMore() {
        isLoadingMore = false;
        getFooter().setState(LoadMoreBean.LoadingMoreState.NO_MORE);
        notifyItemChanged(getFooterPosition());
    }

    public void clearFinishFlag() {
        isLoadingMore = false;
        getFooter().setState(LoadMoreBean.LoadingMoreState.NONE);
        notifyItemChanged(getFooterPosition());
    }

    public void goneLoadMore() {
        isLoadingMore = false;
        getFooter().setState(LoadMoreBean.LoadingMoreState.NONE);
    }

    public boolean canLoadMore() {
        // 没有更多了不在触发
        if (getFooter().getState() == LoadMoreBean.LoadingMoreState.NO_MORE) {
            return false;
        }
        if (getItemCount() == 0) {
            return false;
        }
        // 正在loading中，不在触发
        if (isLoadingMore) {
            return false;
        }
        // 触发加载更多
        return true;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecycler = recyclerView;
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) layoutManager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (isFooterPosition(position)) {
                        return gridManager.getSpanCount();
                    }
                    return 1;
                }
            });
        }
    }

    public void setRetryLoadMoreListener(OnClickRetryListener listener) {
        mRetryLoadMoreListener = listener;
    }

    @Override
    public final void onFooterItemClick(RecyclerView.ViewHolder viewHolder, Object t) {
        if (hasFooter() && getFooter().getState() == LoadMoreBean.LoadingMoreState.LOADED_ERROR) {
            if (mRetryLoadMoreListener != null) {
                mRetryLoadMoreListener.onRetryLoadMore();
            }
        }
    }
}
