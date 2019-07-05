package com.redefine.welike.business.publisher.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.R;
import com.redefine.welike.business.publisher.management.bean.HotTopics;

public class TopicSearchHotTopicItemViewHolder extends BaseRecyclerViewHolder<HotTopics.HotTopic> {
    private final TextView mTopicName;
    private final TextView mTopicDes;

    public TopicSearchHotTopicItemViewHolder(View itemView) {
        super(itemView);
        mTopicName = itemView.findViewById(R.id.topic_search_hot_item_text);
        mTopicDes = itemView.findViewById(R.id.topic_search_hot_item_des);
    }


    @Override
    public void bindViews(RecyclerView.Adapter adapter, HotTopics.HotTopic data) {
        super.bindViews(adapter, data);
        mTopicDes.setText(data.description);

        if (!TextUtils.isEmpty(data.topicName)) {
            SpannableStringBuilder topicName = new SpannableStringBuilder(data.topicName);
            topicName.insert(1, " ");
            mTopicName.setText(topicName);
        } else {
            mTopicName.setText("");
        }

    }
}
