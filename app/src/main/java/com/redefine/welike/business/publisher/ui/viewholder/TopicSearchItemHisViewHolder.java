package com.redefine.welike.business.publisher.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.R;
import com.redefine.welike.base.dao.welike.TopicSearchHistory;

/**
 * Created by liwenbo on 2018/4/11.
 */

public class TopicSearchItemHisViewHolder extends BaseRecyclerViewHolder<TopicSearchHistory> {

    private final TextView mSearchHistoryItem;

    public TopicSearchItemHisViewHolder(View itemView) {
        super(itemView);
        mSearchHistoryItem = itemView.findViewById(R.id.item_title);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, TopicSearchHistory data) {
        super.bindViews(adapter, data);
        mSearchHistoryItem.setText(data.getKeyword());
    }
}