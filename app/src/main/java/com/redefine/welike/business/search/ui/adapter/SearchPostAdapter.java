package com.redefine.welike.business.search.ui.adapter;

import android.view.ViewGroup;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.business.browse.ui.listener.IBrowseClickListener;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter;
import com.redefine.welike.business.feeds.ui.viewholder.BaseFeedViewHolder;
import com.redefine.welike.statistical.EventConstants;

/**
 * Created by liwenbo on 2018/2/11.
 */

public class SearchPostAdapter extends FeedRecyclerViewAdapter {
    private IBrowseClickListener mBrowseClickListener;

    public SearchPostAdapter(IPageStackManager pageStackManager) {
        super(pageStackManager, EventConstants.FEED_PAGE_SEARCH_POSTS);
    }

    public int indexOfPost(PostBase postBase) {
        return mPostBaseList.indexOf(postBase);
    }

    @Override
    public BaseRecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return super.onCreateItemViewHolder(parent, viewType);
    }

    @Override
    protected void onBindItemViewHolder(BaseRecyclerViewHolder holder, int position) {
        super.onBindItemViewHolder(holder, position);

        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        marginLayoutParams.topMargin = 0;
        holder.itemView.setLayoutParams(marginLayoutParams);

        if (holder instanceof BaseFeedViewHolder && mBrowseClickListener != null) {
            ((BaseFeedViewHolder) holder).setBrowseClickListener(mBrowseClickListener);
        }
    }

    public void setBrowseClickListener(IBrowseClickListener listener) {
        mBrowseClickListener = listener;
    }
}
