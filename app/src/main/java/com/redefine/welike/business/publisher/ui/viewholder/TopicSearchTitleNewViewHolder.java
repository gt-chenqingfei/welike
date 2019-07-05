package com.redefine.welike.business.publisher.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.R;

/**
 * Created by liwenbo on 2018/4/10.
 */

public class TopicSearchTitleNewViewHolder extends BaseRecyclerViewHolder<String> {
    private final TextView title,tip;

    public TopicSearchTitleNewViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.header_title);
        tip = itemView.findViewById(R.id.header_tip);

    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, String data) {
        super.bindViews(adapter, data);
        title.setText(data);
    }

    public void setSubTitle(String subTitle) {
        tip.setText(subTitle);
    }
}
