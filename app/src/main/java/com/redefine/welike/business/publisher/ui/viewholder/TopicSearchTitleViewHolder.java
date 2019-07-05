package com.redefine.welike.business.publisher.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.R;

/**
 * Created by liwenbo on 2018/4/10.
 */

public class TopicSearchTitleViewHolder extends BaseRecyclerViewHolder<String> {
    private final TextView mTitleView;
//    private final TextView mSubTitleView;

    public TopicSearchTitleViewHolder(View itemView) {
        super(itemView);
        mTitleView = itemView.findViewById(R.id.header_title);
//        mSubTitleView = itemView.findViewById(R.id.topic_search_sub_title_text);

    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, String data) {
        super.bindViews(adapter, data);
        mTitleView.setText(data);
//        mSubTitleView.setVisibility(View.INVISIBLE);
    }

//    public void setSubTitle(String subTitle) {
//        mSubTitleView.setVisibility(View.VISIBLE);
//        mSubTitleView.setText(subTitle);
//    }
}
