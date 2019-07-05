package com.redefine.commonui.loadmore.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.redefine.commonui.R;
import com.redefine.commonui.loadmore.bean.LoadMoreBean;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.commonui.loadmore.viewholder.LoadMoreViewHolder;
import com.redefine.foundation.utils.LogUtil;

/**
 * Created by liwenbo on 2018/2/6.
 */

public abstract class LoadMoreHeaderAdapter<T, F>  extends HeaderAndFooterRecyclerViewAdapter<BaseRecyclerViewHolder, LoadMoreBean, T, F> implements HeaderAndFooterRecyclerViewAdapter.OnHeaderItemClickListener {
    private boolean isLoadingMore = false;
    private OnClickRetryListener mRetryLoadMoreListener;
    private RecyclerView mRecycler;

    public LoadMoreHeaderAdapter() {
        setHeader(new LoadMoreBean());
        setOnHeaderItemClickListener(this);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mRecycler = null;
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    protected BaseRecyclerViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        return new LoadMoreViewHolder(mInflater.inflate(R.layout.load_more_layout, parent, false));
    }

    @Override
    protected void onBindHeaderViewHolder(BaseRecyclerViewHolder holder, int position) {
        holder.bindViews(this, getHeader());
    }


    public void onLoadMore() {
        isLoadingMore = true;
        getHeader().setState(LoadMoreBean.LoadingMoreState.LOADING);
        notifyItemChanged(getHeaderPosition());
    }

    public void finishLoadMore() {
        isLoadingMore = false;
        getHeader().setState(LoadMoreBean.LoadingMoreState.LOADING);
        notifyItemChanged(getHeaderPosition());
    }

    public void loadError() {
        isLoadingMore = false;
        getHeader().setState(LoadMoreBean.LoadingMoreState.LOADED_ERROR);
        notifyItemChanged(getHeaderPosition());
    }

    public void noMore() {
        isLoadingMore = false;
        getHeader().setState(LoadMoreBean.LoadingMoreState.NO_MORE);
        notifyItemChanged(getHeaderPosition());
    }

    public void clearFinishFlag() {
        isLoadingMore = false;
        getHeader().setState(LoadMoreBean.LoadingMoreState.NONE);
    }

    public void goneLoadMore() {
        isLoadingMore = false;
        getHeader().setState(LoadMoreBean.LoadingMoreState.NONE);
        notifyItemChanged(getHeaderPosition());
    }

    public boolean canLoadMore() {
        // 没有更多了不在触发
        if (getHeader().getState() == LoadMoreBean.LoadingMoreState.NO_MORE) {
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
                    if (isHeaderPosition(position)) {
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
    public final void onHeaderItemClick(RecyclerView.ViewHolder viewHolder, Object t) {
        if (hasHeader() && getHeader().getState() == LoadMoreBean.LoadingMoreState.LOADED_ERROR) {
            if (mRetryLoadMoreListener != null) {
                mRetryLoadMoreListener.onRetryLoadMore();
            }
        }
    }
}
