package com.redefine.welike.business.supertopic.ui.adapter;

import android.view.ViewGroup;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.R;
import com.redefine.welike.business.browse.ui.listener.IBrowseClickListener;
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter;
import com.redefine.welike.business.feeds.ui.viewholder.BaseFeedViewHolder;
import com.redefine.welike.statistical.EventConstants;

public class SuperTopicRecylerViewAdapter extends FeedRecyclerViewAdapter {
    private IBrowseClickListener iBrowseClickListener;

    public SuperTopicRecylerViewAdapter(IPageStackManager pageStackManager, String source) {
        super(pageStackManager, source);
    }

    public void setBrowseClickListener(IBrowseClickListener iBrowseClickListener) {
        this.iBrowseClickListener = iBrowseClickListener;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindItemViewHolder(BaseRecyclerViewHolder holder, int position) {
        super.onBindItemViewHolder(holder, position);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        marginLayoutParams.topMargin = 0;
        holder.itemView.setLayoutParams(marginLayoutParams);
        if (holder instanceof BaseFeedViewHolder) {
            ((BaseFeedViewHolder) holder).dismissTopicCard(true);

            if (iBrowseClickListener != null) {
                ((BaseFeedViewHolder) holder).setBrowseClickListener(iBrowseClickListener);
            }
        }
    }

}
