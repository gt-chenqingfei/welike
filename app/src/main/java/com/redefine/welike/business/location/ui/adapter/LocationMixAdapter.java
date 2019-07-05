package com.redefine.welike.business.location.ui.adapter;

import android.view.ViewGroup;

import com.redefine.commonui.loadmore.bean.BaseHeaderBean;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter;
import com.redefine.welike.statistical.EventConstants;

/**
 * Created by liwenbo on 2018/3/21.
 */

public class LocationMixAdapter extends FeedRecyclerViewAdapter<BaseHeaderBean> {
    public LocationMixAdapter() {
        super(null, EventConstants.FEED_PAGE_LOCATION_LATEST);
    }

    @Override
    protected void onBindItemViewHolder(BaseRecyclerViewHolder holder, int position) {
        super.onBindItemViewHolder(holder, position);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        marginLayoutParams.topMargin = 0;
        holder.itemView.setLayoutParams(marginLayoutParams);
    }
}
